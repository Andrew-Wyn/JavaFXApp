package fx.Controller;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThComp extends Thread{

    @Override
    public void run() {
        for(;;){
            System.out.println("****** ThComp Log ******");
            try {
                ThComp.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public Vector go(byte[] data, String key){
        //prendere bytes da file esterno
        StringBuilder binary = new StringBuilder();
        for (byte b : data)
        {
           int val = b;
           for (int i = 0; i < 8; i++)
           {
              binary.append((val & 128) == 0 ? 0 : 1);
              val <<= 1;
           }
        }

        Vector v = new Vector();


        byte[] bytesk = key.getBytes();
        StringBuilder binaryk = new StringBuilder();

        for (byte b : bytesk)
        {
           int val = b;
           for (int i = 0; i < 8; i++)
           {
              binaryk.append((val & 128) == 0 ? 0 : 1);
              val <<= 1;
           }
        }

        int binlengthk = binaryk.length();
        int appodiffk = 0;
        if(binlengthk < 256){
            appodiffk = 256 - binlengthk;
        }else{
            //TODO dichiarando la differenza tra binlength e 256, vado ad eliminarla,
        }

        /* vado ad aggiungere bit pari a 0 alla fine del blocco binario in chiaro fino al raggiungimento di un blocco divisibile per 256 */
        int numero_aggiuntek = 0;
        int num_blocchik = appodiffk;
        int numero_macrok = num_blocchik;
        while(num_blocchik != 0){
            binaryk.append(0);
            num_blocchik--;
        }

        /* vado ad aggiungere bit pari a 0 alla fine del blocco binario in chiaro fino al raggiungimento di un blocco divisibile per 256 */
        int binlength = binary.length();
        int appodiff = 0;

        if(binlength < 256){
            appodiff = 256 - binlength;
        }else{
            int rest = binary.length()%256;
            for(int i=0; i<256-rest; i++){
                binary.append(0);
            }
        }

        int num = binary.length()/256;
        if(binary.length()%256 != 0){
            num++;
        }

        int numero_aggiunte = 0;
        int num_blocchi = appodiff;
        int numero_macro = num_blocchi;
        while(num_blocchi != 0){
            binary.append(0);
            num_blocchi--;
        }

        System.out.println(""+binary);
        System.out.println(""+binary.length());

        //creare i due array a 4 dim 
        char [][][][]fotoSammlung = new char[4][4][4][4];
        char [][][][]fotoSammlungk = new char[4][4][4][4];

        int bit_ins = 0;
        int bit_insk = 0;
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                for(int x=0; x<4; x++){
                    for(int y=0; y<4; y++){
                        fotoSammlungk[i][j][x][y]=binaryk.charAt(bit_insk);
                        bit_insk++;
                    }
                }
            }
        }

        //azzero per sicurezza
        bit_insk=0;

        /*******************************/

        for(int giga=0; giga<num; giga++){

            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    for(int x=0; x<4; x++){
                        for(int y=0; y<4; y++){
                            fotoSammlung[i][j][x][y]=binary.charAt(bit_ins);
                            //fotoSammlungk[i][j][x][y]=binaryk.charAt(bit_ins);
                            bit_ins++;
                        }
                    }
                }
            }
            if(true){

                for(int iiiii=0; iiiii<2; iiiii++){
                    for(int i=0; i<4; i++){
                        char [][] temp1 = new char[4][4];
                        char [][] tempappo1 = new char[4][4];
                        for(int ii=0; ii<i; ii++){
                            for(int q=0; q<4; q++){
                                for(int z=0; z<4; z++){
                                    for(int p=0; p<4; p++){
                                        if(q==0){
                                            tempappo1[z][p]=fotoSammlung[i][0][z][p];
                                        }
                                        if(q<3){
                                            temp1[z][p]=fotoSammlung[i][q+1][z][p];
                                            fotoSammlung[i][q][z][p] = temp1[z][p];
                                        }else{
                                            fotoSammlung[i][q][z][p] = tempappo1[z][p];
                                        }
                                    }
                                }
                            }  
                        }
                        //livello a 3 dimensioni
                        for(int j=0; j<4; j++){
                            char [] temp2 = new char[4];
                            char [] tempappo2 = new char[4];
                            for(int ii=0; ii<j; ii++){
                                for(int z=0; z<4; z++){
                                    for(int p=0; p<4; p++){
                                        if(z==0){
                                            tempappo2[p]=fotoSammlung[i][j][0][p];
                                        }
                                        if(z<3){
                                            temp2[p]=fotoSammlung[i][j][z+1][p];
                                            fotoSammlung[i][j][z][p] = temp2[p];
                                        }else{
                                            fotoSammlung[i][j][z][p] = tempappo2[p];
                                        }
                                    }
                                }
                            }
                            //livello a 2 dimensioni
                            for(int x=0; x<4; x++){
                                char temp3 = 'a';
                                char tempappo3 = 'a';
                                for(int ii=0; ii<x; ii++){
                                    for(int p=0; p<4; p++){
                                        if(p==0){
                                            tempappo3=fotoSammlung[i][j][x][0];
                                        }
                                        if(p<3){
                                            temp3=fotoSammlung[i][j][x][p+1];
                                            fotoSammlung[i][j][x][p] = temp3;
                                        }else{
                                            fotoSammlung[i][j][x][p] = tempappo3;
                                        }
                                    }
                                }
                                //livello a 1 dimensioni
                                for(int y=0; y<4; y++){

                                    char un = fotoSammlung[i][j][x][y];
                                        char du = fotoSammlungk[i][j][x][y];
                                        if (un==du){
                                            fotoSammlung[i][j][x][y]='0';
                                        } else {
                                            fotoSammlung[i][j][x][y]='1';
                                        }

                                    if(y<3){
                                        //sommo il cubo di posizione i con il cubo di posizione i+1
                                        //somma xor
                                        char un2 = fotoSammlung[i][j][x][y];
                                        char du2 = fotoSammlung[i][j][x][y+1];
                                        if (un2==du2){
                                            fotoSammlung[i][j][x][y]='0';
                                        } else {
                                            fotoSammlung[i][j][x][y]='1';
                                        }
                                    }
                                }
                                if(x<3){
                                    //sommo il cubo di posizione i con il cubo di posizione i+1
                                    for(int y=0; y<4; y++){
                                        //somma xor
                                        char un = fotoSammlung[i][j][x][y];
                                        char du = fotoSammlung[i][j][x+1][y];
                                        if (un==du){
                                            fotoSammlung[i][j][x][y]='0';
                                        } else {
                                            fotoSammlung[i][j][x][y]='1';
                                        }
                                    }
                                }
                            }
                            if(j<3){
                                //sommo il cubo di posizione i con il cubo di posizione i+1
                                for(int x=0; x<4; x++){
                                    for(int y=0; y<4; y++){
                                        //somma xor
                                        char un = fotoSammlung[i][j][x][y];
                                        char du = fotoSammlung[i][j+1][x][y];
                                        if (un==du){
                                            fotoSammlung[i][j][x][y]='0';
                                        } else {
                                            fotoSammlung[i][j][x][y]='1';
                                        }
                                    }
                                }
                            }
                        }
                        if(i<3){
                            //sommo il cubo di posizione i con il cubo di posizione i+1
                            for(int j=0; j<4; j++){
                                for(int x=0; x<4; x++){
                                    for(int y=0; y<4; y++){
                                        //somma xor
                                        char un = fotoSammlung[i][j][x][y];
                                        char du = fotoSammlung[i+1][j][x][y];
                                        if (un==du){
                                            fotoSammlung[i][j][x][y]='0';
                                        } else {
                                            fotoSammlung[i][j][x][y]='1';
                                        }
                                    }
                                }
                            }                    
                        }
                    }
                }
            }

            StringBuilder binaryc = new StringBuilder();

            //metto il vettore a quattro dimensioni in una stringa binaria
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    for(int x=0; x<4; x++){
                        for(int y=0; y<4; y++){
                            binaryc.append(fotoSammlung[i][j][x][y]);
                        }
                    }
                }
            }
            System.out.println(binaryc);
            v.addElement(binaryc);
        }
        binary.setLength(0);
        bit_ins = 0;
        return v;
    }
    
}
