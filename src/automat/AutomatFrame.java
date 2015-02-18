/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automat;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author Иван
 */
public class AutomatFrame extends JFrame {

    public AutomatFrame(List<Condition> automat) {
        super("AutomatFrame");

        final mxGraph graph = new mxGraph();
        

        fillGraph(automat, graph);


        mxIGraphLayout layout = new mxFastOrganicLayout(graph);
        mxGraphComponent graphComponent;
        graphComponent = new mxGraphComponent(graph);
        try {
            layout.execute(graph.getDefaultParent());
        } finally {
            mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

            morph.addListener(mxEvent.DONE, new mxEventSource.mxIEventListener() {
                @Override
                public void invoke(Object arg0, mxEventObject arg1) {
                    graph.getModel().endUpdate();
                }
            });

            morph.startAnimation();
        }




       // graphComponent.setEnabled(false);

        getContentPane().add(graphComponent);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setVisible(true);
    }
    
    private void fillGraph(List<Condition> automat, mxGraph graph) {
        final int c = 50;
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();        
        try {
            List<Object> vertexes = new ArrayList<>();
            for (Condition cond : automat) {
                Object v = graph.insertVertex(parent, null, cond.getName(), c, c, c, c);
                vertexes.add(v);
            }
            int index = 0;
            for (Condition cond : automat) {
                Object from = vertexes.get(index);
                if (cond.isIsInput()) {
                    Object inputV = graph.insertVertex(parent, null, "", c, c, 0, 0);
                    graph.insertEdge(parent, null, "", inputV, from);
                }
                if (cond.isIsOutput()) {
                    Object outputV = graph.insertVertex(parent, null, "", c, c, 0, 0);
                    graph.insertEdge(parent, null, "", from, outputV);
                }
                for (Map.Entry<String, List<Condition>> entry : cond.getRoutes().entrySet()) {
                    String symbol = entry.getKey();
                    List<Condition> list = entry.getValue();
                    for (Condition symbolCond : list) {
                        int indexTo = automat.indexOf(symbolCond);
                        Object to = vertexes.get(indexTo);
                        graph.insertEdge(parent, null, symbol, from, to);
                    }
                }
                index++;
            }
           
            
        } finally {
            graph.getModel().endUpdate();
        }
    }
}
