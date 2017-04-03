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
import java.util.Arrays;
import java.util.Collections;
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
    
    public static BufferedImage prepareImage(BufferedImage image)
    {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, null, 0, 0);
        g2.dispose();
        
        for(int y=0; y<result.getHeight(); y++)
        {
            for(int x=0; x<result.getWidth(); x++)
            {
                if(result.getRGB(x, y)==Color.WHITE.getRGB())
                {
                    result.setRGB(x, y, NEW_WHITE);
                }
                else if(result.getRGB(x, y)==Color.BLACK.getRGB())
                {
                    result.setRGB(x, y, NEW_BLACK);
                }
            }
        }
        
        return result;
}
    
    public static boolean encryptImage(BufferedImage image, int numberOfImages, File path, String prefix)
    {
        boolean result = true;
        int imageCount = 1;
        
        ArrayList<BufferedImage> outputImages = new ArrayList<>();
        int numberOfPixels = image.getWidth()*image.getHeight();
        
        byte[]key = getKey(numberOfPixels);
        
        if(numberOfImages==2)
        {
            Integer[][] share1 = {{NEW_WHITE, NEW_BLACK}, {NEW_BLACK, NEW_WHITE},};
            Integer[][] share2 = {{NEW_BLACK, NEW_WHITE}, {NEW_WHITE, NEW_BLACK}};
            
            BufferedImage cipherImage = expandImage(image);
            BufferedImage keyImage = new BufferedImage(cipherImage.getWidth(), cipherImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for(int y=0; y<image.getHeight(); y++)
            {
                for(int x=0; x<image.getWidth(); x++)
                {
                    if(image.getRGB(x, y)==NEW_WHITE)
                    {
                        for(int row=0; row<2; row++)
                        {
                            for(int col=0; col<2; col++)
                            {
                                if(key[(y*image.getWidth())+x]%2==0)
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, share1[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, share1[row][col]);
                                }
                                else
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, share2[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, share2[row][col]);
                                }
                            }
                        }
                    }
                    else
                    {
                        for(int row=0; row<2; row++)
                        {
                            for(int col=0; col<2; col++)
                            {
                                if(key[(y*image.getWidth())+x]%2==0)
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, share1[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, share2[row][col]);
                                }
                                else
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, share2[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, share1[row][col]);
                                }
                            }
                        }
                    }
                }
            }

            outputImages.add(cipherImage);
            outputImages.add(keyImage);
        }
        
        else if(numberOfImages==4)
        {
            Integer[][] blackShare1 = {{NEW_WHITE, NEW_BLACK, NEW_BLACK}, {NEW_BLACK, NEW_BLACK, NEW_BLACK}, {NEW_WHITE, NEW_WHITE, NEW_WHITE}};
            Integer[][] blackShare2 = {{NEW_WHITE, NEW_BLACK, NEW_WHITE}, {NEW_BLACK, NEW_BLACK, NEW_WHITE}, {NEW_WHITE, NEW_BLACK, NEW_BLACK}};
            Integer[][] blackShare3 = {{NEW_WHITE, NEW_WHITE, NEW_BLACK}, {NEW_BLACK, NEW_BLACK, NEW_WHITE}, {NEW_BLACK, NEW_WHITE, NEW_BLACK}};
            Integer[][] blackShare4 = {{NEW_WHITE, NEW_WHITE, NEW_WHITE}, {NEW_BLACK, NEW_BLACK, NEW_BLACK}, {NEW_BLACK, NEW_BLACK, NEW_WHITE}};
            ArrayList<Integer[][]> blackShare = new ArrayList<>();
            blackShare.add(blackShare1);
            blackShare.add(blackShare2);
            blackShare.add(blackShare3);
            blackShare.add(blackShare4);
            
            Integer[][] whiteShare1 = {{NEW_WHITE, NEW_BLACK, NEW_BLACK}, {NEW_WHITE, NEW_BLACK, NEW_BLACK}, {NEW_WHITE, NEW_BLACK, NEW_WHITE}};
            Integer[][] whiteShare2 = {{NEW_WHITE, NEW_BLACK, NEW_WHITE}, {NEW_BLACK, NEW_BLACK, NEW_BLACK}, {NEW_WHITE, NEW_WHITE, NEW_BLACK}};
            Integer[][] whiteShare3 = {{NEW_WHITE, NEW_BLACK, NEW_WHITE}, {NEW_BLACK, NEW_BLACK, NEW_WHITE}, {NEW_BLACK, NEW_BLACK, NEW_WHITE}};
            Integer[][] whiteShare4 = {{NEW_BLACK, NEW_WHITE, NEW_WHITE}, {NEW_BLACK, NEW_BLACK, NEW_BLACK}, {NEW_WHITE, NEW_BLACK, NEW_WHITE}};
            ArrayList<Integer[][]> whiteShare = new ArrayList<>();
            whiteShare.add(whiteShare1);
            whiteShare.add(whiteShare2);
            whiteShare.add(whiteShare3);
            whiteShare.add(whiteShare4);
            
            BufferedImage share1 = new BufferedImage(image.getWidth()*3, image.getHeight()*3, BufferedImage.TYPE_INT_ARGB);
            BufferedImage share2 = new BufferedImage(image.getWidth()*3, image.getHeight()*3, BufferedImage.TYPE_INT_ARGB);
            BufferedImage share3 = new BufferedImage(image.getWidth()*3, image.getHeight()*3, BufferedImage.TYPE_INT_ARGB);
            BufferedImage share4 = new BufferedImage(image.getWidth()*3, image.getHeight()*3, BufferedImage.TYPE_INT_ARGB);
            BufferedImage[] shares = {share1, share2, share3, share4};
            
            for(int y=0; y<image.getHeight(); y++)
            {
                for(int x=0; x<image.getWidth(); x++)
                {
                    if(image.getRGB(x, y)==NEW_WHITE)
                    {
                        Collections.shuffle(whiteShare, getSecureRandom());
                        int shareSchemeCount = 0;
                        
                        for(BufferedImage share:shares)
                        {
                            Integer[][] shareScheme = whiteShare.get(shareSchemeCount++);
                            
                            for(int row=0; row<3; row++)
                            {
                                for(int col=0; col<3; col++)
                                {
                                    share.setRGB(x*3+col, y*3+row, shareScheme[row][col]);
                                }
                            }
                            
                        }
                    }
                    else
                    {
                        Collections.shuffle(blackShare, getSecureRandom());
                        int shareSchemeCount = 0;
                        
                        for(BufferedImage share:shares)
                        {
                            Integer[][] shareScheme = blackShare.get(shareSchemeCount++);
                            
                            for(int row=0; row<3; row++)
                            {
                                for(int col=0; col<3; col++)
                                {
                                    share.setRGB(x*3+col, y*3+row, shareScheme[row][col]);
                                }
                            }
                            
                        }
                    }
                }
            }
            outputImages.add(shares[0]);
            outputImages.add(shares[1]);
            outputImages.add(shares[2]);
            outputImages.add(shares[3]);
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
    
    private static byte[] getKey(int numberOfElements)
    {
        byte[] key = new byte[numberOfElements];
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
    
    private static SecureRandom getSecureRandom()
    {
        SecureRandom sr;

        try 
        {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            sr = new SecureRandom();
        }
        
        return sr;
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
