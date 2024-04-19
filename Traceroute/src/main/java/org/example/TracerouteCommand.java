package org.example;

import org.shortpasta.icmp2.IcmpPingRequest;
import org.shortpasta.icmp2.IcmpPingResponse;
import org.shortpasta.icmp2.IcmpPingUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TracerouteCommand {
    static private InetAddress destinationAddress;
    static private int ttl = 1;
    static private int fails = 0;
    static private boolean destinationReached = false;
    static public void tracing(String nameWebsite){
        try {
            parseArg(nameWebsite);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String resultLine;
        TakingExpression expression =new TakingExpression();
        try{
            expression.local(nameWebsite);
            if (expression.local(nameWebsite).equals("local")){
                System.out.println(nameWebsite);
                System.out.println("local");
                System.exit(1);
            }
        }catch (NumberFormatException ignored){

        }

        int MAX_HOPS = 30;
        while(!destinationReached && ttl < MAX_HOPS) {
            resultLine = ping();
//System.out.println(resultLine);
            MyTuple<Integer, String> tuple = cutting(resultLine);
            whoIsOutput(tuple);
            ttl++;
        }
//System.exit(0);

    }
    static private MyTuple<Integer, String> cutting(String line){
        List<String> list = new ArrayList<>(List.of(line.split("\\s+")));
        list.removeAll(Arrays.asList("", null));
        String numberOfLineString = list.get(0);
//System.out.println(numberOfLineString);
        String nameSubnet = list.get(1);
        if (Objects.equals(nameSubnet, "*")){
            nameSubnet = " * ";
//System.out.println(999);
        }
        int numberOfLine;

        numberOfLine = Integer.parseInt(numberOfLineString);

        return new MyTuple<>(numberOfLine, nameSubnet);
    }
    static private String whoIs(String ip, String server ){
        String lines = "";
        try {
            Socket socket = new Socket(server, 43);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(Objects.equals(server, "whois.ripe.net")){
                out.println("-B "+ip);
            }else {
                out.println(ip);
            }
            out.flush();

            String line;
            while ((line = in.readLine()) != null) {
//System.out.println(line);
                lines += line;
            }
            socket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return lines;
    }
    static private String whoIs1(String ip){
        String lines = "";
        String m ="";
        try {
            Socket socket = new Socket("whois.iana.org", 43);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(ip);
            out.flush();

            String line;
            while ((line = in.readLine()) != null) {
//System.out.println(line);
                lines += line;
            }
            TakingExpression exp = new TakingExpression();
            m =exp.takingWhois(lines);
            System.out.println(m);
            socket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return m;
    }
    static private void whoIsOutput(MyTuple<Integer, String> tuple){
        System.out.println(tuple.getFirst() + " " + tuple.getSecond());
        if (Objects.equals(tuple.getSecond(), " * ")){
            System.out.println();
            return;
        }
        TakingExpression expression = new TakingExpression();
        if (Objects.equals(expression.local(tuple.getSecond()), "local")){
            System.out.println("local");
            System.out.println();
            return;
        }
        String f =whoIs1(tuple.getSecond());

        String variable = whoIs(tuple.getSecond(),f);
        String Country = expression.takingCountry(variable);
        String Name = expression.takingName(variable);
        String AS = expression.takingOrigin(variable);

        if (Country == null){
            Country = "*";
        }
        if (AS == null){
            AS = "*";
        }
        if (Name == null){
            Name = "*";
        }
        if (Country.equals("EU")){
            String code = expression.takingEU(variable);
            String sub1 = code.substring(0,3);
            String sub2 = code.substring(0,2);
            CountryCheck.experience();
            if (CountryCheck.map.get(sub2)!=null){
//System.out.println(sub2);
                Country = CountryCheck.map.get(sub2);
            }
            if (CountryCheck.map.get(sub1)!=null){
//System.out.println(sub1);
                Country = CountryCheck.map.get(sub1);
            }

        }
        System.out.println("Country: " + Country);
        System.out.println("Name: " + Name);
        System.out.println("AS: " + AS);
        System.out.println();
    }
    public static void main(String[] args) {
        tracing("phoenix.com");
    }

    static private String ping() {
        IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
        request.setHost(destinationAddress.getHostAddress());
        request.setTtl(ttl);
        String resultLine = "";
        IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
        if(response.getTimeoutFlag()) {
            fails++;
//System.out.println(ttl + " *");
            resultLine += ttl + " *" + "\n";
            int MAX_TIMEOUTS = 5;
            if(fails == MAX_TIMEOUTS) {
                System.out.println("Блокировка фаерволом");
                System.exit(1);
            }
        } else {
            fails = 0;
            resultLine += ttl + " " + response.getHost() + "\n";
//System.out.println(resultLine);
        }

        if(response.getSuccessFlag())
            destinationReached = true;
        return resultLine;
    }

    static private void parseArg(String ip) throws UnknownHostException {
        destinationAddress = InetAddress.getByName(ip);
    }
}