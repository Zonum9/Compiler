package regalloc;

import gen.asm.*;

import java.io.PrintWriter;
import java.util.*;

import static gen.asm.OpCode.J;

public class ControlFlowGraph {

    public static class Node{
        Set<Node> successors = new HashSet<>();
        AssemblyItem data;

        @Override
        public String toString() {
            return data.toString();
        }
    }

    public final Node root = new Node();


    public ControlFlowGraph(AssemblyProgram.Section section) {
        buildGraph(section);
        performLiveAnalysis();
    }

    private void buildGraph(AssemblyProgram.Section section){
        List<AssemblyItem> items =new ArrayList<>(section.items.stream()
                .filter(i->!(i instanceof Comment || i instanceof Directive)).toList());
        Node previous = root;
        root.data=items.removeFirst();

        Map<Label,List<Node>> branchesToConnect = new HashMap<>();
        Map<Label,Node> labelNodeMap= new HashMap<>();

        for (AssemblyItem item:items){
            Node curr = new Node();
            if(item instanceof Label lb){
                labelNodeMap.put(lb,curr);
            }
            curr.data=item;
            if(previous!=null){
                previous.successors.add(curr);
            }
            if( item instanceof Instruction.ControlFlow controlFlow){
                switch (controlFlow){
                    //BEQ, BNE
                    case Instruction.BinaryBranch x -> {
                        Label label= x.label;
                        branchesToConnect.computeIfAbsent(label,k -> new ArrayList<>()).add(curr);
                    }

                    //B, BAL, J, JAL
                    case Instruction.Jump x -> {
                        //only jump should be considered for control flow, the rest are for func calls
                        if(x.opcode !=J){
                            break;
                        }
                        Label label= x.label;
                        branchesToConnect.computeIfAbsent(label,k -> new ArrayList<>()).add(curr);
                        curr=null;
                    }
                    //JR, JALR
                    case Instruction.JumpRegister ignored -> curr=null;

                    //BEQZ, BGEZ, BGEZAL, BGTZ, BLEZ, BLTZ, BLTZAL, BNEZ
                    case Instruction.UnaryBranch x -> {
                        Label label= x.label;
                        branchesToConnect.computeIfAbsent(label,k -> new ArrayList<>()).add(curr);
                    }
                }
            }
            previous= curr;
        }

        branchesToConnect.forEach((label, nodes) -> {
                    Node lblNode = labelNodeMap.get(label);
                    for(Node n:nodes){
                        n.successors.add(lblNode);
                    }
                }
        );
    }

    public final HashMap<Node,HashSet<Register>> liveIn = new HashMap<>();
    public final HashMap<Node,HashSet<Register>> liveOut = new HashMap<>();

    private void performLiveAnalysis(){
            List<Node> nodesPostOrder = getNodesPostOrder();
            for(Node n: nodesPostOrder){
                liveIn.put(n,new HashSet<>());
                liveOut.put(n,new HashSet<>());
            }

            HashMap<Node,HashSet<Register>> tempIn = new HashMap<>();
            HashMap<Node,HashSet<Register>> tempOut = new HashMap<>();
            do{
                for(Node n:nodesPostOrder){
                    tempIn.put(n,liveIn.get(n));
                    tempOut.put(n,liveOut.get(n));

                    //union of successor's live in is our live out
                    HashSet <Register> newLiveOutN= new HashSet<>();
                    for(Node s:n.successors){
                        newLiveOutN.addAll(liveIn.get(s));
                    }
                    liveOut.put(n,newLiveOutN);

                    //everything that we use and (everything that we emit - what we create) is our live in
                    HashSet <Register> newLiveInN;
                    if(n.data instanceof Instruction inst){
                        newLiveInN= new HashSet<>(newLiveOutN);
                        newLiveInN.remove(inst.def());
                        newLiveInN.addAll(inst.uses()
                                .stream()
                                .filter(Register::isVirtual).toList()
                        );
                    }
                    else{
                        newLiveInN=new HashSet<>(newLiveOutN);
                    }
                    liveIn.put(n,newLiveInN);
                }
            }while (!(liveIn.equals(tempIn) && liveOut.equals(tempOut)));

            //todo make sure this is correct
            //for all nodes n, add join their defined sets with their live out sets (handles "dead" instructions)
            for(Node n: nodesPostOrder){
                if(n.data instanceof Instruction inst) {
                    Register def=inst.def();
                    if(def != null && def.isVirtual()) {
                        liveOut.get(n).add(def);
                        for (Node successor : n.successors) {
                            liveIn.get(successor).add(def);
                        }
                    }
                }
            }
    }

    private PrintWriter writer;
    private int nodeCnt;
    private HashMap<ControlFlowGraph.Node,String> visited;

    public void print(PrintWriter writer){
        visited=new HashMap<>();
        nodeCnt=0;
        this.writer=writer;
        visit(root);
    }

    private String visit(Node n){
        if(visited.containsKey(n)){
            return visited.get(n);
        }
        nodeCnt++;
        String nid= ""+n.hashCode();
        visited.put(n,nid);

        switch (n.data){
            case Comment ignore -> {}
            case Directive ignore -> {}
            case AssemblyItem x -> {
                writer.println(nid + "[label=\""+nodeCnt+": " + x + "\"];");
            }
        }

        for (ControlFlowGraph.Node suc : n.successors){
            String s = visit(suc);
            writer.println(nid + "->"+ s+";");
            for (Register r:liveOut.get(n)) {
                writer.println(nid + "->" + s + "[label = \""+r+"\",color=\"red\"];");
            }
            for (Register r :liveIn.get(suc)){
                writer.println(nid + "->" + s + "[label = \""+r+"\",color=\"blue\"];");
            }
        }
        return nid;
    }

    public List<Node> getNodesPostOrder() {
        HashSet<Node>visited = new HashSet<>();
        Stack<Node> stack = new Stack<>();
        dfs(root,visited,stack);
        return stack;
    }

    private void dfs(Node node, HashSet<Node> visited, Stack<Node> stack) {
        visited.add(node);
        for (Node s:node.successors){
            if(!visited.contains(s)){
                dfs(s,visited,stack);
            }
        }
        stack.push(node);
    }





}
