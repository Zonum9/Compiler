package regalloc;

import gen.asm.Instruction;
import gen.asm.Register;

import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InterferenceGraph {

    private final HashMap<Register, HashSet<Register>> interference;
    public final HashMap<Register, Integer> colorings;
    public final HashMap<Register,Long> regUseCount;

    public InterferenceGraph(ControlFlowGraph g,int k) {
        interference = new HashMap<>();
        colorings = new HashMap<>();
        List<ControlFlowGraph.Node> controlNodes = new ArrayList<>(g.getNodesReversePreOrder());
        regUseCount=getRegUseCount(controlNodes);
        for(ControlFlowGraph.Node n: controlNodes){
            for (Register register:g.liveIn.get(n)) {
                interference.computeIfAbsent(register,r->new HashSet<>()).addAll(g.liveIn.get(n));
            }

            for (Register register:g.liveOut.get(n)) {
                interference.computeIfAbsent(register,r->new HashSet<>()).addAll(g.liveOut.get(n));
            }
        }
        interference.forEach((reg,set)->set.remove(reg));
        //k registers available
        colorGraph(k);
    }

    public HashSet<Register> spilled;
    private void colorGraph(int k){
        spilled = new HashSet<>();
        Stack<Register> stack = new Stack<>();
        HashSet<Register> removed = new HashSet<>();

        while(true){
            getDegLessThanK(k,stack,removed);
            if(removed.size()==interference.size()){
                break;
            }

            Register maxDegreeReg = interference.entrySet().stream()
                    .filter(pair->!removed.contains(pair.getKey()))
                    .max((p1,p2)-> {
                                int comp = Long.compare(
                                        p1.getValue().stream().filter(x -> !removed.contains(x)).count(),
                                        p2.getValue().stream().filter(x -> !removed.contains(x)).count()
                                );
                                if (comp!=0)
                                    return comp;
                                return -Long.compare(regUseCount.get(p1.getKey()),regUseCount.get(p2.getKey()));
                            }
                    ).get().getKey(); //should never be empty
            removed.add(maxDegreeReg);
            spilled.add(maxDegreeReg);
            getDegLessThanK(k,stack,removed);
        }

        colorVars(stack,k);
    }

    private HashMap<Register, Long> getRegUseCount(List<ControlFlowGraph.Node> controlNodes){
        HashMap<Register,Long> sectionRegs = new HashMap<>();
        controlNodes.stream()
                .filter(x-> x.data instanceof Instruction)
                .map(x->(Instruction)x.data)
                .forEach(ins->
                        ins.registers().forEach(reg->
                                sectionRegs.put(reg,sectionRegs.getOrDefault(reg, 0L)+1)
                        )
                );
        return sectionRegs;
    }

    private void colorVars(Stack<Register> stack,int k){
        while (!stack.empty()){
            Register reg = stack.pop();
            Set<Integer> neighbourColors = interference.get(reg).stream()
                    .map(colorings::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            for (int i = 0; i < k; i++) {
                if(neighbourColors.contains(i)){
                    continue;
                }
                colorings.put(reg,i);
                break;
            }
        }
    }

    private void getDegLessThanK(int k, Stack<Register> stack,HashSet<Register> removed){
        while (true){
            Optional<Register> optReg=interference.entrySet()
                    .stream()
                    .filter(
                            pair-> {
                                if(removed.contains(pair.getKey())){
                                    return false;
                                }
                                long neighCount =pair.getValue().stream()
                                        .filter(x -> !removed.contains(x)).count();
                                return neighCount<k;
                            }
                            )
                    .map(Map.Entry::getKey)
                    .findFirst();
            if(optReg.isEmpty()){
                break;
            }
            Register reg= optReg.get();
            removed.add(reg);
            stack.push(reg);
        }
    }




    private PrintWriter writer;
    private HashMap<Register,String> visited;
    private HashSet<String> drawn;
    public void print(PrintWriter writer){
        visited=new HashMap<>();
        drawn = new HashSet<>();
        this.writer=writer;
        for (Register register : interference.keySet()) {
            visit(register);
        }
    }

    private String visit(Register r){
        if(visited.containsKey(r)){
            return visited.get(r);
        }
        String nid = r.toString();
        visited.put(r, nid);
        String color;
        if(colorings.containsKey(r)) {
            color = intToColor(colorings.get(r));
        }
        else {
            color= "white";
        }
        writer.println(nid + "[label=\""+ r + "\",style=\"filled\", fillcolor  = \""+color+"\"];");

        for (Register suc : interference.get(r)){
            String s = visit(suc);
            String edge=nid + "--"+ s+";";
            if(drawn.contains(s + "--"+ nid+";") || drawn.contains(edge)){
                drawn.add(edge);
                continue;
            }

            writer.println(edge);
            drawn.add(edge);
        }

        return nid;
    }

    private String intToColor(int i){
        String[] colors = {
                "Red",
                "Green",
                "Blue",
                "Yellow",
                "Orange",
                "Purple",
                "Pink",
                "Cyan",
                "Magenta",
                "Turquoise",
                "Lime",
                "Indigo",
                "Gold",
                "Silver",
                "Brown",
                "Coral"
        };
        return colors[i];

    }


}
