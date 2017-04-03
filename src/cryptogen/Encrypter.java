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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;

/**
 *
 * @author Tanesh Manjrekar
 */
public class Encrypter 
{
    private static final int NEW_WHITE = new Color(255, 255, 255, 255).getRGB();
    private final static int NEW_BLACK = new Color(0, 0, 0, 0).getRGB();
    
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
        for(String lineText:text.split("\n"))
        {
            g2.drawString(lineText, 0, textY+=textHeight);
        }
        g2.dispose();
        
        return result;
    }
    
    public static boolean encryptImage(BufferedImage image, int numberOfImages, File path, String prefix)
    {
        boolean result = true;
        int imageCount = 1;
        
        ArrayList<BufferedImage> outputImages = new ArrayList<>();
        BufferedImage cipherImage = expandImage(image);
        int numberOfPixels = image.getWidth()*image.getHeight();
        
        byte[]key = getKey(numberOfPixels);
        int rowCount = 0;
        
        if(numberOfImages==2)
        {
            BufferedImage keyImage = new BufferedImage(cipherImage.getWidth(), cipherImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for(int y=0; y<image.getHeight(); y++)
            {
                for(int x=0; x<image.getWidth(); x++)
                {
                    Color keyColor = key[(y*rowCount)+x]%2==0 ? new Color(NEW_WHITE) : new Color(NEW_BLACK);

                    if(keyColor.getRGB()==NEW_WHITE)
                    {
                        keyImage.setRGB(x*2, y*2, NEW_BLACK);
                        keyImage.setRGB(x*2, y*2+1, NEW_WHITE);
                        keyImage.setRGB(x*2+1, y*2, NEW_BLACK);
                        keyImage.setRGB(x*2+1, y*2+1, NEW_WHITE); 
                    }
                    else
                    {
                        keyImage.setRGB(x*2, y*2, NEW_WHITE);
                        keyImage.setRGB(x*2, y*2+1, NEW_BLACK);
                        keyImage.setRGB(x*2+1, y*2, NEW_WHITE);
                        keyImage.setRGB(x*2+1, y*2+1, NEW_BLACK);
                    }

                    cipherImage.setRGB(x*2, y*2, colorXOR(image.getRGB(x, y),keyImage.getRGB(x*2, y*2)));
                    cipherImage.setRGB(x*2, y*2+1, colorXOR(image.getRGB(x, y), keyImage.getRGB(x*2, y*2+1)));
                    cipherImage.setRGB(x*2+1, y*2, colorXOR(image.getRGB(x, y), keyImage.getRGB(x*2+1, y*2)));
                    cipherImage.setRGB(x*2+1, y*2+1, colorXOR(image.getRGB(x, y), keyImage.getRGB(x*2+1, y*2+1)));

                }
                rowCount++;
            }

            outputImages.add(cipherImage);
            outputImages.add(keyImage);
        }
        
        else if(numberOfImages==4)
        {
            
        }
        
        Iterator<BufferedImage> imageIterator = outputImages.iterator();
        while(imageIterator.hasNext())
        {
            result = result && writeImageToFile(imageIterator.next(), new File(path.getPath() + "/" + prefix + imageCount++ + ".png"));
        }
        
        return result;
    }
    
    private static BufferedImage expandImage(BufferedImage image)
    {
        BufferedImage expandedImage = new BufferedImage(image.getWidth()*2, image.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
        
        for(int x=0; x<image.getWidth(); x++)
        {
            for(int y=0; y<image.getHeight(); y++)
            {

                if(image.getRGB(x, y)==Color.WHITE.getRGB())
                {
                    expandedImage.setRGB(x*2, y*2, NEW_WHITE);
                    expandedImage.setRGB(x*2, y*2+1, NEW_WHITE);
                    expandedImage.setRGB(x*2+1, y*2, NEW_WHITE);
                    expandedImage.setRGB(x*2+1, y*2+1, NEW_WHITE);
                }
                else
                {
                    expandedImage.setRGB(x*2, y*2, NEW_BLACK);
                    expandedImage.setRGB(x*2, y*2+1, NEW_BLACK);
                    expandedImage.setRGB(x*2+1, y*2, NEW_BLACK);
                    expandedImage.setRGB(x*2+1, y*2+1, NEW_BLACK);
                }
            }
        }
        
        return expandedImage;
    }
    
    private static byte[] getKey(int pixelsPerImage)
    {
        byte[] key = new byte[pixelsPerImage];
        SecureRandom sr;
        
        try 
        {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            sr = new SecureRandom();
        }
        
        sr.nextBytes(key);
        
        return key;
    }
    
    private static boolean writeImageToFile(BufferedImage image, File path)
    {
        boolean result = false;
        
        try
        {
            result = ImageIO.write(image, "png", path);
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        }
        
        return result;
    }
    
    private static int colorXOR(int color1, int color2)
    {
        Color c1 = new Color(color1);
        Color c2 = new Color(color2);
        
        if(c1.getRed()==c2.getRed() && c1.getGreen()==c2.getGreen() && c1.getBlue()==c2.getBlue())
        {
            return NEW_WHITE;
        }
        return NEW_BLACK;
    }
}
