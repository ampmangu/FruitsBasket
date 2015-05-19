package utilidades;


import simulador.*;
import com.sun.j3d.demos.utils.scenegraph.traverser.ProcessNodeInterface;
import com.sun.j3d.demos.utils.scenegraph.traverser.TreeScan;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.TransformGroup;
import com.sun.j3d.utils.picking.PickTool;
import java.util.Enumeration;

public class CapabilitiesMDL {

    String dd;
       private static ProcessNodeInterface capabilityProcessor = new ProcessNodeInterface(){
            public void processNode(Node node)  {
                if (node instanceof Group)                {
                    node.setCapability(Group.ALLOW_CHILDREN_READ);
                    if (node instanceof TransformGroup)          {
                        node.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
                    }
                }
                else if (node instanceof Shape3D){
                    Shape3D s = (Shape3D) node;
                    s.setUserData("figura_MDL_"+CapabilitiesMDL.identificador);
                    PickTool.setCapabilities(s, PickTool.INTERSECT_FULL);
                    s.setPickable(true);

                    s.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
                    Appearance app = s.getAppearance();
                    app.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
                    app.clearCapabilityIsFrequent(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
                    app.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
                    app.clearCapabilityIsFrequent(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
                    app.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);
                    app.clearCapabilityIsFrequent(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);

                    int tuCount = s.getAppearance().getTextureUnitCount();
                    for (int i = 0; i < tuCount; i++){
                    	TextureUnitState tu = s.getAppearance().getTextureUnitState(i);
                        if (tu != null) {
                            tu.setCapability(TextureUnitState.ALLOW_STATE_READ);
                            tu.getTexture().setCapability(Texture.ALLOW_ENABLE_WRITE);
                            tu.getTextureAttributes().setCapability(TextureAttributes.ALLOW_MODE_WRITE);
                            tu.getTexture().clearCapabilityIsFrequent(Texture.ALLOW_ENABLE_WRITE);
                            tu.getTextureAttributes().clearCapabilityIsFrequent(TextureAttributes.ALLOW_MODE_WRITE);
                        }
                    }
                }
            }
    };
   static int identificador=-1;
 public static void setCapabilities(BranchGroup bg, int identificador_)  {
        identificador = identificador_;
        TreeScan.findNode(bg, new Class[] {Group.class, Shape3D.class}, capabilityProcessor, false, false);
    }

 public static void etiquetarTodo (Node nodoR, int nivel, Juego fp, String etiqueta){

     if (nodoR instanceof BranchGroup){
              Enumeration enumeracion =( (BranchGroup) nodoR).getAllChildren(); ////.getAllChildren();
              while (enumeracion.hasMoreElements()){
                  Object objeto = enumeracion.nextElement();   //System.out.println("Voy por nivel: "+nivel+"  en:"+ objeto.getClass().getName());
                  if (objeto instanceof TransformGroup){  //System.out.println("Voy por: "+objeto.getClass().getName());
                      int nuevoNivel = nivel +1;
                      etiquetarTodo ( (Node) objeto, nuevoNivel, fp, etiqueta);
                  } if ((objeto instanceof Shape3D) && (nivel <100)){
                      ((Shape3D) objeto).setUserData(etiqueta);
                  //ColisionDetector2 cd = new ColisionDetector2 ((Shape3D) objeto, fp.limites, fp);
                     //  ((Shape3D) objeto).setCapability(Node.ENABLE_PICK_REPORTING);
                      //PickTool.setCapabilities( ((Shape3D) objeto), PickTool.INTERSECT_FULL);
                    //  ((Shape3D) objeto).setPickable(true);
                  //    objRoot.addChild(cd);
                      //partesPersonaje1.add((Shape3D) objeto);
}}}
          if (nodoR instanceof TransformGroup){
              Enumeration enumeracion =( (TransformGroup) nodoR).getAllChildren(); ////.getAllChildren();
              while (enumeracion.hasMoreElements()){
                  Object objeto = enumeracion.nextElement();   //System.out.println("Voy por nivel: "+nivel+"  en:"+ objeto.getClass().getName());
                  if (objeto instanceof TransformGroup){  //System.out.println("Voy por: "+objeto.getClass().getName());
                      int nuevoNivel = nivel +1;
                      etiquetarTodo ( (Node) objeto, nuevoNivel, fp, etiqueta);
                  }
                 if (objeto instanceof BranchGroup){  //System.out.println("Voy por: "+objeto.getClass().getName());
                      int nuevoNivel = nivel +1;
                      etiquetarTodo ( (Node) objeto, nuevoNivel, fp, etiqueta);
                  }
                  if ((objeto instanceof Shape3D) && (nivel <100)){
                      ((Shape3D) objeto).setUserData(etiqueta);
                  //ColisionDetector2 cd = new ColisionDetector2 ((Shape3D) objeto, fp.limites, fp);
                     //  ((Shape3D) objeto).setCapability(Node.ENABLE_PICK_REPORTING);
                 //    PickTool.setCapabilities( ((Shape3D) objeto), PickTool.INTERSECT_FULL);
                 //     ((Shape3D) objeto).setPickable(true);
                  //    objRoot.addChild(cd);
                      //partesPersonaje1.add((Shape3D) objeto);
}}}
 }
}


