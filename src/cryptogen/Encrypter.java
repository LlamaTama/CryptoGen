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
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Tanesh Manjrekar
 */
public class Encrypter 
{
    private static final int NEW_WHITE = new Color(255, 255, 255, 255).getRGB();
    private static final int NEW_BLACK = new Color(0, 0, 0, 0).getRGB();
    private static final SecureRandom SECURE_RANDOM = getSecureRandom();
    
    
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
            Integer[][][] sharePair1 = {share1, share2};
            
            Integer[][] share3 = {{NEW_WHITE, NEW_BLACK}, {NEW_WHITE, NEW_BLACK},};
            Integer[][] share4 = {{NEW_BLACK, NEW_WHITE}, {NEW_BLACK, NEW_WHITE}};
            Integer[][][] sharePair2 = {share3, share4};
            
            Integer[][] share5 = {{NEW_WHITE, NEW_WHITE}, {NEW_BLACK, NEW_BLACK},};
            Integer[][] share6 = {{NEW_BLACK, NEW_BLACK}, {NEW_WHITE, NEW_WHITE}};
            Integer[][][] sharePair3 = {share5, share6};
            
            ArrayList<Integer[][][]> sharePairs = new ArrayList<>();
            sharePairs.add(sharePair1);
            sharePairs.add(sharePair2);
            sharePairs.add(sharePair3);
            
            BufferedImage cipherImage = ImageOps.expandImage(image);
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
                                Collections.shuffle(sharePairs, SECURE_RANDOM);
                                Integer[][][] sharePair = sharePairs.get(0);
                                Integer[][] selectedShare1 = sharePair[0];
                                Integer[][] selectedShare2 = sharePair[1];
                                
                                if(key[(y*image.getWidth())+x]%2==0)
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, selectedShare1[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, selectedShare1[row][col]);
                                }
                                else
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, selectedShare2[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, selectedShare2[row][col]);
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
                                Collections.shuffle(sharePairs, SECURE_RANDOM);
                                Integer[][][] sharePair = sharePairs.get(0);
                                Integer[][] selectedShare1 = sharePair[0];
                                Integer[][] selectedShare2 = sharePair[1];
                                
                                if(key[(y*image.getWidth())+x]%2==0)
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, selectedShare1[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, selectedShare2[row][col]);
                                }
                                else
                                {
                                    keyImage.setRGB(x*2+col, y*2+row, selectedShare2[row][col]);
                                    cipherImage.setRGB(x*2+col, y*2+row, selectedShare1[row][col]);
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
                        Collections.shuffle(whiteShare, SECURE_RANDOM);
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
                        Collections.shuffle(blackShare, SECURE_RANDOM);
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
            result = result && ImageOps.writeImageToFile(imageIterator.next(), new File(path.getPath() + "/" + prefix + imageCount++ + ".png"));
        }
        
        return result;
    }
    
    
    private static byte[] getKey(int numberOfElements)
    {
        byte[] key = new byte[numberOfElements];
        
        SECURE_RANDOM.nextBytes(key);
        
        return key;
    }
    
    private static SecureRandom getSecureRandom()
    {
        SecureRandom secureRandom;

        try 
        {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            secureRandom = new SecureRandom();
        }
        
        return secureRandom;
    }
    
    public static int getNewWhite() 
    {
        return NEW_WHITE;
    }

    public static int getNewBlack() 
    {
        return NEW_BLACK;
    }
}
