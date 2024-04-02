package regalloc;

import gen.asm.Register;

import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InterferenceGraph {

    private final HashMap<Register, HashSet<Register>> interference;
    public final HashMap<Register, Integer> colorings;

    public InterferenceGraph(ControlFlowGraph g,int k) {
        interference = new HashMap<>();
        colorings = new HashMap<>();
        List<ControlFlowGraph.Node> controlNodes = new ArrayList<>(g.getNodesReversePreOrder());

        for(ControlFlowGraph.Node n: controlNodes){
            for (Register register:g.liveIn.get(n)) {
                interference.computeIfAbsent(register,r->new HashSet<>()).addAll(g.liveIn.get(n));
//                interference.get(register).remove(register); //todo check if this is correct
            }

            for (Register register:g.liveOut.get(n)) {
                interference.computeIfAbsent(register,r->new HashSet<>()).addAll(g.liveOut.get(n));
//                interference.get(register).remove(register);
            }
        }
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
                    .max((p1,p2)->
                        Long.compare(
                                p1.getValue().stream().filter(x -> !removed.contains(x)).count(),
                                p2.getValue().stream().filter(x -> !removed.contains(x)).count()
                        )
                    ).get().getKey(); //should never be empty
            removed.add(maxDegreeReg);
            spilled.add(maxDegreeReg);
            getDegLessThanK(k,stack,removed);
        }

        colorVars(stack,k);
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
                    .findAny();
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
