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
        int numberOfPixels = cipherImage.getWidth()*cipherImage.getHeight();
        for(int i=0; i<numberOfImages-1; i++)
        {
            byte[]key = getKey(numberOfPixels);
            BufferedImage outputImage = new BufferedImage(cipherImage.getWidth(), cipherImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int rowCount = 0;
            for(int y=0; y<cipherImage.getHeight(); y++)
            {
                for(int x=0; x<cipherImage.getWidth(); x++)
                {
                    Color keyColor = key[(y*rowCount)+x]%2==0 ? new Color(255, 255, 255, 127) : new Color(0, 0, 0, 127);
                    Color colorVal = new Color(cipherImage.getRGB(x, y));
                    int redVal = keyColor.getRed()^colorVal.getRed();
                    int greenVal = keyColor.getGreen()^colorVal.getGreen();
                    int blueVal = keyColor.getBlue()^colorVal.getBlue();
                    
                    cipherImage.setRGB(x, y, new Color(redVal, greenVal, blueVal, 127).getRGB());
                    outputImage.setRGB(x, y, keyColor.getRGB());
                }
                rowCount++;
            }
            outputImages.add(outputImage);
        }
        
        outputImages.add(0, cipherImage);
        
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
        int newWhite = new Color(255, 255, 255, 127).getRGB();
        int newBlack = new Color(0, 0, 0, 127).getRGB();
        
        for(int x=0; x<image.getWidth(); x++)
        {
            for(int y=0; y<image.getHeight(); y++)
            {
                
                if(image.getRGB(x, y)==Color.WHITE.getRGB())
                {
                    expandedImage.setRGB(x*2, y*2, newWhite);
                    expandedImage.setRGB(x*2, y*2+1, newWhite);
                    expandedImage.setRGB(x*2+1, y*2, newWhite);
                    expandedImage.setRGB(x*2+1, y*2+1, newWhite);
                }
                else
                {
                    expandedImage.setRGB(x*2, y*2, newBlack);
                    expandedImage.setRGB(x*2, y*2+1, newBlack);
                    expandedImage.setRGB(x*2+1, y*2, newBlack);
                    expandedImage.setRGB(x*2+1, y*2+1, newBlack);
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
}
