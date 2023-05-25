package TipoPublicacion;

public class Texto extends Publicacion{
    String fuente;
    int cantCaracteres, tamañoFuente;

    public Texto(String nombre, String descripcion, int cantMG, String fuente, int cantCaracteres, int tamañoFuente) {
        nombre = this.getNombre();
        descripcion = this.getDescripcionPost();
        cantMG = this.getCantMG();
        fuente = this.fuente;
        cantCaracteres = this.cantCaracteres;
        tamañoFuente = this.tamañoFuente;
    }


    public String getFuente() {
        return this.fuente;
    }

    public int getCantCaracteres() {
        return this.cantCaracteres;
    }

    public int getTamañoFuente() {
        return this.tamañoFuente;
    }
}
