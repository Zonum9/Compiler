package regalloc;

import gen.asm.*;

import java.io.PrintWriter;
import java.util.*;

import static gen.asm.OpCode.J;
import static gen.asm.OpCode.JAL;

public class ControlFlowGraph {

    public static class Node{
        Optional<Boolean> condition = Optional.empty();
        Set<Node> successors = new HashSet<>();
        Set<Node> predecessors = new HashSet<>();
        AssemblyItem data;

        @Override
        public String toString() {
            return data.toString();
        }
    }

    private <T> Set<T> intersect(Set<T> s1,Set<T> s2){
        Set<T> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        return intersection;
    }

    private <T> Set<T> union(Set<T> s1,Set<T> s2){
        Set<T> union = new HashSet<>(s1);
        union.addAll(s2);
        return union;
    }

    private <T> Set<T> difference(Set<T> s1,Set<T> s2){
        Set<T> difference = new HashSet<>(s1);
        difference.retainAll(s2);
        return difference;
    }

    public final Node root = new Node();
//todo store graph leafs
    public ControlFlowGraph(AssemblyProgram.Section section) {
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
                curr.predecessors.add(previous);
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
                    lblNode.predecessors.addAll(nodes);
                    for(Node n:nodes){
                        n.successors.add(lblNode);
                    }
                }
        );

    }


    private PrintWriter writer;
    private int nodeCnt;
    private HashMap<Node,String> visited;
    public void print(PrintWriter writer){
        visited=new HashMap<>();
        nodeCnt=0;
        this.writer=writer;
        writer.println("digraph ast {");
        visit(root);
        writer.println("}");
    }
    private String visit(Node n){
        if(visited.containsKey(n)){
            return visited.get(n);
        }
        nodeCnt++;
        String nid= "Node"+nodeCnt;
        visited.put(n,nid);

        switch (n.data){
            case Comment ignore -> {}
            case Directive ignore -> {}
            case AssemblyItem x -> {
                writer.println(nid + "[label=\"" + x + "\"];");
            }
        }

        ArrayList<String> children = new ArrayList<>();
        for (Node suc : n.successors){
            children.add(visit(suc));
        }
        // write out edges
        for( String s :children){
            writer.println(nid + "->"+ s+";");
            writer.println(s + "->"+ nid+" [color=\"red\"];");
        }
        return nid;
    }


}
