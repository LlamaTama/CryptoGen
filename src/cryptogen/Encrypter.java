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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.5));
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
    
    public static boolean encryptImage(BufferedImage image, int numberOfImages, File path)
    {
        boolean result = false;
        
        byte[][] keys = getKeys(numberOfImages-1, image.getWidth()*image.getHeight());
        
        
        return result;
    }
    
    private static byte[][] getKeys(int numberOfKeys, int bitsPerImage)
    {
        byte[][] keys = new byte[numberOfKeys][];
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
            byte key[] = new byte[bitsPerImage];
            sr.nextBytes(key);
            
            for(int j=0; j<bitsPerImage; j++)
            {
                key[j] = (byte) Math.abs(key[j]%2);
            }
            
            keys[i] = key;
        }
        
        return keys;
    }
}
