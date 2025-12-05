package DATOS;
import java.util.ArrayList;
import java.util.List;
import LOGICA.Instalacion;

public class instalacionDb {
    public static List<Instalacion> obtenerTodas() {
    
        List<Instalacion> lista = new ArrayList<>();
        lista.add(new Instalacion(1, "Cancha de Fútbol 5"));
        lista.add(new Instalacion(2, "Piscina Olímpica"));
        lista.add(new Instalacion(3, "Cancha de Tenis A"));
        lista.add(new Instalacion(4, "Gimnasio Techado"));
        return lista;
    }
}