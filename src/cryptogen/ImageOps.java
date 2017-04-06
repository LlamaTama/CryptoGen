/*
 * Copyright (C) 2017 Tanesh Manjrekar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cryptogen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Tanesh Manjrekar
 */
public class ImageOps 
{   
    public static BufferedImage generateImage(String text, int fontSize, int height, int width) 
    {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        int textHeight = fm.getHeight();
        int textY = 0;
        for (String lineText : text.split("\n")) {
            g2.drawString(lineText, 0, textY += textHeight);
        }
        g2.dispose();
        return result;
    }

    static BufferedImage expandImage(BufferedImage image) 
    {
        BufferedImage expandedImage = new BufferedImage(image.getWidth() * 2, image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) == Color.WHITE.getRGB()) {
                    expandedImage.setRGB(x * 2, y * 2, Encrypter.getNewWhite());
                    expandedImage.setRGB(x * 2, y * 2 + 1, Encrypter.getNewWhite());
                    expandedImage.setRGB(x * 2 + 1, y * 2, Encrypter.getNewWhite());
                    expandedImage.setRGB(x * 2 + 1, y * 2 + 1, Encrypter.getNewWhite());
                } else {
                    expandedImage.setRGB(x * 2, y * 2, Encrypter.getNewBlack());
                    expandedImage.setRGB(x * 2, y * 2 + 1, Encrypter.getNewBlack());
                    expandedImage.setRGB(x * 2 + 1, y * 2, Encrypter.getNewBlack());
                    expandedImage.setRGB(x * 2 + 1, y * 2 + 1, Encrypter.getNewBlack());
                }
            }
        }
        return expandedImage;
    }

    static boolean writeImageToFile(BufferedImage image, File path) 
    {
        boolean result = false;
        try 
        {
            result = ImageIO.write(image, "png", path);
        } 
        catch (IOException ioe) 
        {
            System.out.println(ioe);
        }
        return result;
    }

    public static BufferedImage prepareImage(BufferedImage image) 
    {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, null, 0, 0);
        g2.dispose();
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                if (result.getRGB(x, y) == Color.WHITE.getRGB()) {
                    result.setRGB(x, y, Encrypter.getNewWhite());
                } else if (result.getRGB(x, y) == Color.BLACK.getRGB()) {
                    result.setRGB(x, y, Encrypter.getNewBlack());
                }
            }
        }
        return result;
    }

}
