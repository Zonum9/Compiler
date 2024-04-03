package regalloc;

import gen.asm.*;

import java.io.PrintWriter;
import java.util.*;

import static gen.asm.OpCode.J;

public class ControlFlowGraph {

    public static class Node{
        Set<Node> successors = new HashSet<>();
        AssemblyItem data;

        public Node(AssemblyItem data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    private Node root;


    public ControlFlowGraph(AssemblyProgram.Section section) {
        buildGraph(section);
        performLiveAnalysis();
    }

    private void buildGraph(AssemblyProgram.Section section){
        List<AssemblyItem> items =new ArrayList<>(section.items.stream()
                .filter(i->!(i instanceof Comment || i instanceof Directive)).toList());
        root= new Node(items.removeFirst());
        Node previous = root;

        Map<Label,List<Node>> branchesToConnect = new HashMap<>();
        Map<Label,Node> labelNodeMap= new HashMap<>();

        for (AssemblyItem item:items){
            Node curr = new Node(item);
            if(item instanceof Label lb){
                labelNodeMap.put(lb,curr);
            }
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
                        Label label= x.label;
                        branchesToConnect.computeIfAbsent(label,k -> new ArrayList<>()).add(curr);
                    }
                    //JR, JALR
                    case Instruction.JumpRegister ignored -> {}

                    //BEQZ, BGEZ, BGEZAL, BGTZ, BLEZ, BLTZ, BLTZAL, BNEZ
                    case Instruction.UnaryBranch x -> {
                        Label label= x.label;
                        branchesToConnect.computeIfAbsent(label,k -> new ArrayList<>()).add(curr);
                    }
                }
            }
            previous= curr;
        }

        for (Map.Entry<Label, List<Node>> entry : branchesToConnect.entrySet()) {
            Label label = entry.getKey();
            List<Node> nodes = entry.getValue();
            Node lblNode = labelNodeMap.get(label);
            if(lblNode == null)
                continue;
            for (Node n : nodes) {
                n.successors.add(lblNode);
            }
        }
    }

    public final HashMap<Node,HashSet<Register>> liveIn = new HashMap<>();
    public final HashMap<Node,HashSet<Register>> liveOut = new HashMap<>();

    private void performLiveAnalysis(){
            List<Node> nodesPostOrder = getNodesReversePreOrder();
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

            //for all nodes n, join their defined sets with their live out sets (handles "dead" instructions)
            for(Node n: nodesPostOrder){
                if(n.data instanceof Instruction inst) {
                    Register def=inst.def();
                    if(def != null && def.isVirtual()) {
                        liveOut.get(n).add(def);
                    }
                }
            }
    }

    private PrintWriter writer;
    private int nodeCnt;
    private HashMap<ControlFlowGraph.Node,String> visited;

    private HashMap<Node,Integer> order;

    public void print(PrintWriter writer){
        order = new HashMap<>();
        List<Node> l= getNodesReversePreOrder();
        for (int i = 0; i < l.size(); i++) {
            order.put(l.get(i),i);
        }
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
                writer.println(nid + "[label=\""+order.get(n)+": " + x + "\"];");
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

    public List<Node> getNodesReversePreOrder() {//fixme changed this dramatically
        HashSet<Node>visited = new HashSet<>();
        LinkedList<Node> queue = new LinkedList<>();
        dfs(root,visited,queue);
        Collections.reverse(queue);
        return queue;
    }

    private void dfs(Node node, HashSet<Node> visited, Queue<Node> queue) {
        visited.add(node);
        queue.add(node);
        for (Node s:node.successors){
            if(!visited.contains(s)){
                dfs(s,visited,queue);
            }
        }
    }





}
