/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.util;

import java.util.Comparator;

/**
 *
 * @author max
 */
public class PAGEFileComparator implements Comparator<String>
{

    static public final String SPLIT_BY_NUMBER_PRESERVE_DEL = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";

    public Integer valueOf(String str)
    {
        try
        {
            return Integer.parseInt(str);
        } catch (NumberFormatException e)
        {
            return null;
        }
    }

    @Override
    public int compare(String arg0, String arg1)
    {
        final String[] s0 = arg0.split(SPLIT_BY_NUMBER_PRESERVE_DEL);
        final String[] s1 = arg1.split(SPLIT_BY_NUMBER_PRESERVE_DEL);

        int i = 0;
        while (i < s0.length && i < s1.length)
        {
            int c = 0;

            Integer i0 = valueOf(s0[i]);
            Integer i1 = valueOf(s1[i]);
            if (i0 != null && i1 != null)
            {
                c = i0.compareTo(i1);
            } else
            {
                c = s0[i].compareToIgnoreCase(s1[i]);
            }
            if (c != 0)
            {
                return c;
            }
            ++i;
        };
        return new Integer(arg0.length()).compareTo(arg1.length());
    }
}
