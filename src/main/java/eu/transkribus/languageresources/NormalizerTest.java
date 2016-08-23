/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources;

import java.text.Normalizer;

/**
 *
 * @author max
 */
public class NormalizerTest
{
    public static void main(String[] args) {
//        String s_ö = "schön";
//        String s_ö_nfd = Normalizer.normalize(s_ö, Normalizer.Form.NFD);
//        String s_ö_nfc = Normalizer.normalize(s_ö, Normalizer.Form.NFC);
//        System.out.println(s_ö);
//        System.out.println(Normalizer.normalize(s_ö, Normalizer.Form.NFD));
//        System.out.println(Normalizer.normalize(s_ö, Normalizer.Form.NFD).length());
//
//        String s = "schön";
//        System.out.println(s);
//        System.out.println(s.length());
//        System.out.println(Normalizer.normalize(s, Normalizer.Form.NFD).length());
////        System.out.println(Normalizer.normalize(s, Normalizer.Form.NFD).equals(s_ö_nfd));
////        System.out.println(Normalizer.normalize(s, Normalizer.Form.NFC).equals(s_ö_nfc));
////        System.out.println(Normalizer.normalize(s, Normalizer.Form.NFD).equals(s_ö_nfc));
////        System.out.println(Normalizer.normalize(s, Normalizer.Form.NFC).equals(s_ö_nfd));
////        System.out.println(s_ö.equals(s));
//        
//        String klammerRus = "о";
//        String klammerLat = "o";
//        
//        System.out.println(klammerLat.equals(klammerRus));
//
//        klammerRus = Normalizer.normalize(klammerRus, Normalizer.Form.NFD);
//        klammerLat = Normalizer.normalize(klammerLat, Normalizer.Form.NFD);
//        
//        System.out.println(klammerLat.equals(klammerRus));
        
        String s = "a\nb";
        StringBuilder sb = new StringBuilder();
        
        System.out.println(s.length());
        
        for(int i = 0; i < s.length(); i++)
            sb.append(s.charAt(i));
        
        System.out.println(sb.toString());
    }
}
