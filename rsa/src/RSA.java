import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

public class RSA {
    private static final int e = 65537;

    public void encryption(String pathText, String pathPublicKey) throws IOException {
        String[] publicKey = readKey(pathPublicKey).split(" ");
        byte[] textBytes = readByte(pathText);
        StringBuilder result = new StringBuilder();
        int keyLength = new BigInteger(publicKey[1]).bitLength();
        int k = (keyLength + 1) / 32;

        int l = textBytes.length;
        System.out.println(textBytes.length*8);


        int remainingBytes = textBytes.length % k;
        if (remainingBytes != 0) {
            int paddingBytes = k - remainingBytes;
            byte[] paddedTextBytes = new byte[textBytes.length + paddingBytes];
            System.arraycopy(textBytes, 0, paddedTextBytes, 0, textBytes.length);
            textBytes = paddedTextBytes;
        }
        int l2 = textBytes.length;

        //StringBuilder bin = new StringBuilder();
        double res=0;

        for (int i = 0; i <= textBytes.length - k; i += k) {
            //Instant start = Instant.now();

            BigInteger c = new BigInteger(Arrays.copyOfRange(textBytes, i, i + k));
            c = powFast(c, new BigInteger(publicKey[0]), new BigInteger(publicKey[1]));
            //Instant stop = Instant.now();
            //res+=(Duration.between(start,stop).toMillis());

            result.append(c);
           //bin.append(c.abs().toString(2));

//            for (int j = 0; j < String.valueOf(c).length(); j++) {
//                int character = String.valueOf(c).charAt(j);
//                String binary = String.format("%8s", Integer.toBinaryString(character & 0xFF)).replace(' ', '0');
//                bin += binary;
//            }
            if (i < textBytes.length - k) {
                result.append('\n');
            }

        }

        //System.out.println(res);
        //int lenEnc = result.toString().getBytes().length;
        //System.out.println("k = " + (double)lenEnc/textBytes.length);
        //writeResult("encrypted_3.txt", bin.toString());
        writeResult("encrypted.txt", result.toString());
        result.setLength(0);
        System.out.println("the encrypted text in the file encrypted.txt");
    }


    public void decryption(String pathEncrypted, String pathPrivateKey){
        String[] privateKey = readKey(pathPrivateKey).split(" ");
        StringBuilder result = new StringBuilder();
        double res = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathEncrypted))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Instant start = Instant.now();
                BigInteger m = new BigInteger(line);
                m = powFast(m, new BigInteger(privateKey[0]), new BigInteger(privateKey[1]));
                Instant stop = Instant.now();
                res+=(Duration.between(start,stop).toMillis());
                result.append(new String(m.toByteArray()));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        System.out.println(res);
        writeResult("decrypted.txt", result.toString());
        result.setLength(0);
        System.out.println("the decrypted text in the file decrypted.txt");
    }

    public void task6 (int keyLength){
        double [] r = new double[11];
        r[0] = 0.25;
        double[] time = new double[11];

        for (int i = 0; i < r.length; i++) {

            BigInteger p = BigInteger.probablePrime((int)(r[i]*keyLength), new Random());
            BigInteger q = BigInteger.probablePrime((int)((1-r[i])*keyLength), new Random());
            BigInteger n = p.multiply(q);

            long startTime = System.currentTimeMillis();
            methodPolard(n);
            long endTime = System.currentTimeMillis();
            time[i] = (double)(endTime-startTime)/1000.0;

            if(i!=r.length-1){
                r[i+1] = r[i]+0.025;
            }
        }
        XYSeries series = new XYSeries("Data");
        for (int i = 0; i < r.length; i++) {
            series.add(r[i],time[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        Chart chart = new Chart();
        chart.createGraph("", "r", "t, c", dataset);

        System.out.println(Arrays.toString(r));
        System.out.println(Arrays.toString(time));

    }

    public void task7_enc() throws IOException {

        int textLength [] = new int[8];
        double time256_enc[] = new double[8];
        double time256_dec[] = new double[8];

        double time512_enc[] = new double[8];
        double time512_dec[] = new double[8];

        double time1024_enc[] = new double[8];
        double time1024_dec[] = new double[8];

        Instant start; Instant stop;
        double resTime = 0;

        for (int i = 0; i < textLength.length; i++) {

            start = Instant.now();
            encryption("1000.txt", "public256.txt");
            stop = Instant.now();
            resTime = Duration.between(start, stop).toMillis();
            time256_enc[i] = resTime;

            start = Instant.now();
            decryption("encrypted.txt", "private256.txt");
            stop = Instant.now();
            resTime = Duration.between(start, stop).toMillis();
            time256_dec[i] = resTime;

            start = Instant.now();
            encryption("x.txt", "public512.txt");
            stop = Instant.now();
            resTime = Duration.between(start, stop).toMillis();
            time512_enc[i] = resTime;

            start = Instant.now();
            decryption("encrypted.txt", "private512.txt");
            stop = Instant.now();
            resTime = Duration.between(start, stop).toMillis();
            time512_dec[i] = resTime;

            start = Instant.now();
            encryption("x.txt", "public1024.txt");
            stop = Instant.now();
            resTime = Duration.between(start, stop).toMillis();
            time1024_enc[i] = resTime;

            start = Instant.now();
            decryption("encrypted.txt", "private1024.txt");
            stop = Instant.now();
            resTime = Duration.between(start, stop).toMillis();
            time1024_dec[i] = resTime;
        }

        XYSeries series256_enc = new XYSeries("256");
        XYSeries series512_enc = new XYSeries("512");
        XYSeries series1024_enc = new XYSeries("1024");
        XYSeries series256_dec = new XYSeries("256");
        XYSeries series512_dec = new XYSeries("512");
        XYSeries series1024_dec = new XYSeries("1024");

        for (int i = 0; i < textLength.length; i++) {
            series256_enc.add(textLength[i], time256_enc[i]);
            series512_enc.add(textLength[i], time512_enc[i]);
            series1024_enc.add(textLength[i], time1024_enc[i]);

            series256_dec.add(textLength[i], time256_dec[i]);
            series512_dec.add(textLength[i], time512_dec[i]);
            series1024_dec.add(textLength[i], time1024_dec[i]);
        }

        XYSeriesCollection dataset256_enc = new XYSeriesCollection(series256_enc);
        XYSeriesCollection dataset512_enc = new XYSeriesCollection(series512_enc);
        XYSeriesCollection dataset1024_enc = new XYSeriesCollection(series1024_enc);

        XYSeriesCollection dataset256_dec = new XYSeriesCollection(series256_dec);
        XYSeriesCollection dataset512_dec = new XYSeriesCollection(series512_dec);
        XYSeriesCollection dataset1024_dec = new XYSeriesCollection(series1024_dec);

        XYDataset[] datasets_enc = {dataset256_enc, dataset512_enc, dataset1024_enc};
        XYDataset[] datasets_dec = {dataset256_dec, dataset512_dec, dataset1024_dec};

        Color [] colors = {Color.RED, Color.GREEN, Color.BLUE};

        Chart chart = new Chart();
        chart.createCombinedGraph("t_enc(V)", "length", "time", datasets_enc, colors);
        chart.createCombinedGraph("t_dec(V)", "length", "time", datasets_dec, colors);

        System.out.println(1);

    }

    public void task8() throws IOException {
        int length = (30000-10000)/1000;
        int textLength [] = new int[length];
        double k_256[] = new double[length];
        double k_512[] = new double[length];
        double k_1024[] = new double[length];


        for (int i = 0, j = 10000; i < textLength.length; i++, j+=1000) {
            textLength[i] = j;
            Random random = new Random();
            BigInteger x = new BigInteger(j, random);
            writeResult("x.txt", String.valueOf(x));
            double file_length =0;
            double textBitLength = readByte("x.txt").length*8;

            encryption("x.txt", "public256.txt");
            //File file_256 = new File("encrypted.txt");
            file_length = readByte("encrypted.txt").length*8;
            k_256[i] = file_length/textBitLength;

            encryption("x.txt", "public512.txt");
            //File file_512 = new File("encrypted.txt");
            file_length = readByte("encrypted.txt").length*8;
            k_512[i] = file_length/textBitLength;

            encryption("x.txt", "public1024.txt");
            //File file_1024 = new File("encrypted.txt");
            file_length = readByte("encrypted.txt").length*8;
            k_1024[i] = file_length/textBitLength;
        }

        XYSeries series256 = new XYSeries("k_256");
        XYSeries series512 = new XYSeries("k_512");
        XYSeries series1024 = new XYSeries("k_1024");

        for (int i = 0; i < textLength.length; i++) {
            series256.add(textLength[i], k_256[i]);
            series512.add(textLength[i], k_512[i]);
            series1024.add(textLength[i], k_1024[i]);
        }

        XYSeriesCollection dataset256 = new XYSeriesCollection(series256);
        XYSeriesCollection dataset512 = new XYSeriesCollection(series512);
        XYSeriesCollection dataset1024 = new XYSeriesCollection(series1024);


        XYDataset[] datasets = {dataset256, dataset512, dataset1024};

        Color [] colors = {Color.RED, Color.GREEN, Color.BLUE};

        Chart chart = new Chart();
        chart.createCombinedGraph("k", "length", "k_v", datasets, colors);


    }

    public BigInteger methodPolard(BigInteger n){
        //BigInteger x = BigInteger.TWO;
        BigInteger x = randomBigInteger(BigInteger.ONE, n.subtract(BigInteger.TWO), n.bitLength());
        BigInteger y = BigInteger.ONE;
        BigInteger i = BigInteger.ZERO;
        BigInteger stage = BigInteger.TWO;

        while(n.gcd(x.subtract(y).abs()).compareTo(BigInteger.ONE) == 0){
            if (i.compareTo(stage) == 0){
                y=x;
                stage = stage.multiply(BigInteger.TWO);
            }
            x= x.multiply(x).add(BigInteger.ONE).remainder(n);
            i = i.add(BigInteger.ONE);
        }
        return n.gcd(x.subtract(y).abs());
    }

    private BigInteger randomBigInteger(BigInteger min, BigInteger max, int bitLength) {
        Random rnd = new Random();
        BigInteger res;
        do {
            res = new BigInteger(bitLength, rnd);
        } while (res.compareTo(min) < 0 || res.compareTo(max) > 0);
        return res;
    }


    public void keyGeneration(int keyLength){
        BigInteger p = BigInteger.ZERO;
        BigInteger q = BigInteger.ZERO;
        BigInteger n = BigInteger.ZERO;
        BigInteger funcEuler = BigInteger.ZERO;

        while( funcEuler.gcd(BigInteger.valueOf(e)).compareTo(BigInteger.ONE) != 0 ){
           p = BigInteger.probablePrime(keyLength / 2, new Random());
           q = BigInteger.probablePrime(keyLength / 2, new Random());
           n = p.multiply(q);
           funcEuler = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        }

        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("n = " + n);
        System.out.println("e = " + e);

        writeKey("public.txt", String.valueOf(e), String.valueOf(n));
        System.out.println("GCD(φ(n),e) = " + funcEuler.gcd(BigInteger.valueOf(e)));

        BigInteger d = extendedEuclidAlg(BigInteger.valueOf(e), funcEuler);
        writeKey("private.txt", String.valueOf(d), String.valueOf(n));
        System.out.println("d = " + d);
        System.out.println("(e·d) mod φ(n) = " + d.multiply(BigInteger.valueOf(e)).remainder(funcEuler));
    }

    private BigInteger powFast(BigInteger x, BigInteger d, BigInteger n){
        /**
         * x^d (mod n)
         */

        BigInteger y = BigInteger.ONE;
        while(d.compareTo(BigInteger.ZERO)>0){
            if (d.remainder(BigInteger.TWO)!=BigInteger.ZERO){
                y = y.multiply(x).remainder(n);
            }
            d = d.divide(BigInteger.TWO);
            x = x.multiply(x).remainder(n);
        }
        return y;
    }

    private BigInteger extendedEuclidAlg(BigInteger m, BigInteger p){
        BigInteger a = m, b = p;
        BigInteger u1 = BigInteger.ONE, v1 = BigInteger.ZERO;
        BigInteger u2 = BigInteger.ZERO, v2 = BigInteger.ONE;

        while(b!=BigInteger.ZERO){
            BigInteger q = a.divide(b);
            BigInteger r = a.remainder(b);
            a=b; b =r;
            r = u2;
            u2 = u1.subtract(q.multiply(u2));
            u1=r;
            r=v2;
            v2=v1.subtract(q.multiply(v2));
            v1=r;
        }
        if (u1.compareTo(BigInteger.ZERO) == -1){
            return u1.add(p);
        }else{
            return u1;
        }
    }
    private byte[] readByte(String path) throws IOException {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private String readBin(String path) throws IOException {
//        StringBuilder binaryStringBuilder = new StringBuilder();
//
//        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
//            int c;
//            while((c=reader.read())!=-1){
//                String binary = String.format("%8s", Integer.toBinaryString(c & 0xFF)).replace(' ', '0');
//                binaryStringBuilder.append(binary);
//            }
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//        return binaryStringBuilder.toString();
//    }

    private String binaryToString(String binaryInput) {
        StringBuilder textBuilder = new StringBuilder();

        String result="";
        int paddingLength = 8 - (binaryInput.length() % 8);
        if (paddingLength != 8) {
            for (int i = 0; i < paddingLength; i++) {
                textBuilder.append('0');
            }
            textBuilder.append(binaryInput);
        }

        for (int i = 0; i < textBuilder.length(); i += 8) {
            String binaryChunk = textBuilder.substring(i, Math.min(i + 8, textBuilder.length()));
            int charCode = Integer.parseInt(binaryChunk, 2);
            char character = (char) charCode;
            result+=character;
        }
        return result;
    }


    private void writeResult(String path, String result){
        try(FileWriter writer = new FileWriter(path))
        {
            writer.write(result);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void writeKey(String path, String e, String n){
        try(FileWriter writer = new FileWriter(path))
        {
            writer.write(e+" ");
            writer.write(n);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private String readKey(String pathKey){
        String result = "";
        try(FileReader reader = new FileReader(pathKey))
        {
            int c;
            while((c=reader.read())!=-1){
                result+=(char)c;
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        return result;
    }

}
