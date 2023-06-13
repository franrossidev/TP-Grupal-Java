package GUI;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import Perfil.Album;
import TipoPublicacion.Publicacion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Albumvisual extends JDialog {
    private JPanel contentPane;
    private JButton agregar;
    private JButton eliminar;
    private JComboBox<Object> comboBoxAlbumes;
    private JPanel box;
    private JButton albumButton;
    private JButton subalbumButton;
    private List<Album> listaAlbumes;

    private List<Publicacion> listaPublicacion;
    private Document documentoXML;
    public Albumvisual(List<Album> listaAlbumes,List<Publicacion> listaPublicacion) {

        setTitle("Album");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(agregar);
        this.listaAlbumes = listaAlbumes;
        this.listaPublicacion = listaPublicacion;
        mostrarAlbumes();

        agregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAgregar();
            }
        });

        eliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEliminar();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onX();
            }
        });

        // call on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onX();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        try {
            // Cargar el archivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            documentoXML = dBuilder.parse("proyecto alg II/java/src/Swing/Album.xml");
            documentoXML.getDocumentElement().normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onX(){
        dispose();
    }
    private void onAgregar() {
        String nombreAlbum = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo álbum:", "Agregar Álbum", JOptionPane.PLAIN_MESSAGE);
        if (nombreAlbum != null && !nombreAlbum.isEmpty()) {
            Album nuevoAlbum = new Album(nombreAlbum);
            listaAlbumes.add(nuevoAlbum);
            agregarAlbumXML(nombreAlbum);
            mostrarAlbumes();
        }
    }

    private void onEliminar() {
        String nombreAlbum = JOptionPane.showInputDialog(this, "Ingrese el nombre del álbum a eliminar:", "Eliminar Álbum", JOptionPane.PLAIN_MESSAGE);
        if (nombreAlbum != null && !nombreAlbum.isEmpty()) {
            Album albumEliminado = null;
            for (Album album : listaAlbumes) {
                if (album.getNombreAlbum().equals(nombreAlbum)) {
                    albumEliminado = album;
                    break;
                }
            }
            if (albumEliminado != null) {
                listaAlbumes.remove(albumEliminado);
                JOptionPane.showMessageDialog(this, "Álbum eliminado correctamente.", "Eliminar Álbum", JOptionPane.INFORMATION_MESSAGE);
                eliminarAlbumXML(nombreAlbum);
                mostrarAlbumes();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el álbum especificado.", "Eliminar Álbum", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void eliminarAlbumXML(String nombreAlbum) {
        try {
            NodeList albumNodes = documentoXML.getElementsByTagName("album");
            for (int i = 0; i < albumNodes.getLength(); i++) {
                Node albumNode = albumNodes.item(i);
                if (albumNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element albumElement = (Element) albumNode;
                    Node nombreNode = albumElement.getElementsByTagName("nombre").item(0);
                    String nombre = nombreNode.getTextContent();
                    if (nombre.equals(nombreAlbum)) {
                        albumNode.getParentNode().removeChild(albumNode);
                        guardarCambiosXML();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void agregarAlbumXML(String nombreAlbum) {
        try {
            Element albumElement = documentoXML.createElement("album");
            Element nombreElement = documentoXML.createElement("nombre");
            nombreElement.appendChild(documentoXML.createTextNode(nombreAlbum));
            albumElement.appendChild(nombreElement);

            Node albumsNode = documentoXML.getElementsByTagName("albums").item(0);
            albumsNode.appendChild(albumElement);

            guardarCambiosXML();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void guardarCambiosXML() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(documentoXML);
            StreamResult result = new StreamResult("proyecto alg II/java/src/Swing/Album.xml");
            transformer.transform(source, result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void cargarDatosDesdeXML() {
        try {
            NodeList albumNodes = documentoXML.getElementsByTagName("album");
            for (int i = 0; i < albumNodes.getLength(); i++) {
                Node albumNode = albumNodes.item(i);
                if (albumNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element albumElement = (Element) albumNode;

                    // Obtener el nombre del álbum
                    String nombreAlbum = getTextValue(albumElement, "nombre");
                    Album album = new Album(nombreAlbum);

                    // Cargar la lista principal
                    Element listaElement = (Element) albumElement.getElementsByTagName("lista").item(0);
                    NodeList publicacionNodes = listaElement.getElementsByTagName("publicacion");
                    for (int j = 0; j < publicacionNodes.getLength(); j++) {
                        Element publicacionElement = (Element) publicacionNodes.item(j);
                        Publicacion publicacion = crearPublicacionDesdeElement(publicacionElement);
                        album.agregarPublicacion(publicacion);
                    }

                    // Cargar la sublista
                    Element sublistaElement = (Element) albumElement.getElementsByTagName("sublista").item(0);
                    if (sublistaElement != null) {
                        NodeList subpublicacionNodes = sublistaElement.getElementsByTagName("publicacion");
                        for (int j = 0; j < subpublicacionNodes.getLength(); j++) {
                            Element subpublicacionElement = (Element) subpublicacionNodes.item(j);
                            Publicacion subpublicacion = crearPublicacionDesdeElement(subpublicacionElement);
                            album.agregarSubpublicacion(subpublicacion);
                        }
                    }
                    listaAlbumes.add(album);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Publicacion crearPublicacionDesdeElement(Element publicacionElement) {

    }

    private String getTextValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element tagElement = (Element) nodeList.item(0);
            if (tagElement != null && tagElement.getFirstChild() != null) {
                return tagElement.getFirstChild().getNodeValue();
            }
        }
        return "";
    }
    private void mostrarAlbumes(){

        comboBoxAlbumes.removeAllItems();
        for (Album album : listaAlbumes) {
            comboBoxAlbumes.addItem(album.getNombreAlbum());
        }
        box.setLayout(new FlowLayout());
        box.add(comboBoxAlbumes);
        box.revalidate();
        box.repaint();
    }

}