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

import com.sun.openpisces.AlphaConsumer;
import java.awt.AlphaComposite;
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
    public static BufferedImage generateImage(String text, int fontSize, int height, int width, int numberOfImages)
    {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = result.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1/numberOfImages));
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setPaint(Color.BLACK);
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
    
    public static BufferedImage prepareImage(BufferedImage image, int numberOfImages)
    {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = result.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1/numberOfImages));
        g2.drawImage(image, null, 0, 0);
        g2.dispose();
        
        return result;
    }
    
    public static boolean encryptImage(BufferedImage image, int numberOfImages, File path, String prefix)
    {
        boolean result = true;
        int imageCount = 1;
        
        ArrayList<BufferedImage> outputImages = new ArrayList<>();
        
        byte[][] keys = getKeys(numberOfImages-1, image.getWidth()*image.getHeight());
        
        for(int i=0; i<numberOfImages-1; i++)
        {
            byte[] key = keys[i];
            BufferedImage keyImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int rowCount = 0;
            for(int y=0; y<image.getHeight(); y++)
            {
                for(int x=0; x<image.getWidth(); x++)
                {
                    int redVal, blueVal, greenVal, alphaVal = 255/numberOfImages, keyColor;
                    
                    if(key[(y*rowCount)+x]==1)
                    {
                        redVal = Color.WHITE.getRed();
                        blueVal = Color.WHITE.getBlue();
                        greenVal = Color.WHITE.getGreen();
                        keyColor = (alphaVal<<24) | (redVal<<16) | (greenVal<<8) | blueVal;
                    }
                    else
                    {
                        redVal = Color.BLACK.getRed();
                        blueVal = Color.BLACK.getBlue();
                        greenVal = Color.BLACK.getGreen();
                        keyColor = (alphaVal<<24) | (redVal<<16) | (greenVal<<8) | blueVal;
                    }
                    
                    image.setRGB(x, y, (image.getRGB(x, y)^keyColor));
                    keyImage.setRGB(x, y, keyColor);
                }
            }
            outputImages.add(keyImage);
        }
        
        outputImages.add(0, image);
        
        Iterator<BufferedImage> imageIterator = outputImages.iterator();
        while(imageIterator.hasNext())
        {
            result = result && writeImageToFile(imageIterator.next(), new File(path.getPath() + "/" + prefix + imageCount++ + ".png"));
        }
        
        return result;
    }
    
    private static byte[][] getKeys(int numberOfKeys, int pixelsPerImage)
    {
        byte[][] keys = new byte[numberOfKeys][pixelsPerImage];
        SecureRandom sr;
        try 
        {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            sr = new SecureRandom();
        }
        
        for(int i=0; i<numberOfKeys; i++)
        {
            byte key[] = new byte[pixelsPerImage];
            sr.nextBytes(key);
            
            for(int j=0; j<pixelsPerImage; j++)
            {
                keys[i][j] = (byte) Math.abs(key[j]%2);
            }
        }
        
        return keys;
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
}
