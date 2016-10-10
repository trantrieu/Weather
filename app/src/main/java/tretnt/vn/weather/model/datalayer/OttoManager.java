package tretnt.vn.weather.model.datalayer;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Trieu on 9/2/2016.
 */
public class OttoManager {

    private static final Set<OttoInterface> target = new HashSet<>();

    static public void register(OttoInterface o){
        target.add(o);
    }

    static public void unRegister(OttoInterface o){
        target.remove(o);
    }

    static public void pushMessage(Object object){
        Iterator<OttoInterface> ite = target.iterator();
        while(ite.hasNext()){
            OttoInterface ottoInterface = ite.next();
            ottoInterface.onReceiveObject(object);
        }
    }

    public interface OttoInterface{
        void onReceiveObject(Object object);
    }

}
