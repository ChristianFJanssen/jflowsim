package jflowsim.view.img;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ImageUtilities {

    public static URL getImageFile(String filename) {
        String[] fileType = {"png", "gif", "jpg", "jpeg"};

        for (int i = 0; i < fileType.length; i++) {
            URL url = filename.getClass().getResource("/jflowsim/view/img/" + filename + "."+fileType[i]);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    public static ImageIcon createImageIcon(String filename, int w, int h) {
        ImageIcon icon = new ImageIcon(getImageFile(filename));
        Image image = icon.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public static ImageIcon createImageIcon(String filename) {
        return new ImageIcon(getImageFile(filename));
    }
}
