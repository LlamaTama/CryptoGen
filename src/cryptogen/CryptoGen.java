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

import javax.swing.SwingUtilities;

/**
 *
 * @author Tanesh Manjrekar
 */
public class CryptoGen 
{
    public static void main(String args[])
    {
        //Run GUI on EDT
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                CryptoFrame.createFrame();
            }
        });
        
    }
}
