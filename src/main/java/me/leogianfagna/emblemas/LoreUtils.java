package me.leogianfagna.emblemas;

public class LoreUtils {
    
    public static String emblemaRaridade(int raridade) {
        StringBuilder raridadeImpressao = new StringBuilder();
    
        for (int i = 0; i < raridade; i++) {
            raridadeImpressao.append("§eઘ");
        }
    
        for (int i = raridade; i < 6; i++) {
            raridadeImpressao.append("§8ઘ");
        }
    
        return raridadeImpressao.toString();
    }    
}
