package com.example.guiro.togeather.helper;

public class Base64Custom {

    public static String codificarBase64(String texto){

        return android.util.Base64.encodeToString(texto.getBytes(),
                android.util.Base64.DEFAULT).replaceAll("(\\n|\\r)","" );
    }

    public static String decodificarBase64(String textoCodificado){

        return new String(android.util.Base64.decode(textoCodificado, android.util.Base64.DEFAULT));
    }
}
