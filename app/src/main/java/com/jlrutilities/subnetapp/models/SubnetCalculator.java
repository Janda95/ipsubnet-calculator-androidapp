package com.jlrutilities.subnetapp.models;


//** Subnet Calculator Logic and Operations */
public class SubnetCalculator {

  public SubnetCalculator(){}


  //** Trims IP using cidr, setting numbers after cutoff to 0. */
  public String trimCidrIp(String binaryIp, int cidr){
    String binaryNum = binaryIp;
    StringBuilder builderBinaryNum = new StringBuilder(binaryNum);


    // Set binary substring to 0 after cidr
    for(int i = cidr; i < binaryNum.length(); i++){
      builderBinaryNum.setCharAt(i, '0');
    }


    binaryNum = builderBinaryNum.toString();
    return binaryNum;
  }


  //** Transitions IP String to Binary String. */
  public String ipFormatToBinary(String binaryIp){
    String[] stringArray = binaryIp.split("\\.");
    Integer[] intArray = new Integer[4];

    for (int i = 0; i < 4; i++) {
      try {
        intArray[i] = Integer.parseInt(stringArray[i]);
      } catch (NumberFormatException nfe) {}
    }

    int decimalNum = intArray[3] + (intArray[2] * 256) + (intArray[1] * 65536) + (intArray[0] * 16777216);
    String binaryNum = String.format("%32s", Integer.toBinaryString(decimalNum)).replace(' ', '0');
    return binaryNum;
  }


  //** Transitions Binary String to IP String. */
  public String ipBinaryToFormat(String binaryIp){
    String[] arr = new String[4];

    for(int i = 0; i < arr.length; i++) {
      String temp = binaryIp.substring(i*8, (i*8)+8);
      int number = Integer.parseInt(temp, 2);
      String ipInt = Integer.toString(number);
      arr[i] = ipInt;
    }

    String ipFormat = arr[0] + "." + arr[1] + "." + arr[2] + "." + arr[3];
    return ipFormat;
  }


  //** Split Ip in String format. */
  public String ipSplit(String binaryIp, int cidr){
    // Assuming cidr is not 32
    char[] splitOne = binaryIp.toCharArray();
    splitOne[cidr] = '1';
    String value = new String(splitOne);
    return value;
  }


  //**  Generates Subnet Mask String. */
  public String subnetMask(int cidr){
    char[] arr = new char[32];

    for(int i = 0; i < arr.length; i++){
      if (i < cidr) {
        arr[i] = '1';
      } else {
        arr[i] = '0';
      }
    }

    String theString = new String(arr);
    theString = ipBinaryToFormat(theString);
    return theString;
  }


  //** Generates Broadcast Address String given IP and CIDR. */
  public String broadcastAddress(String binaryIp, int cidr){
    if(cidr == 32){
      ipBinaryToFormat(binaryIp);
      return ipBinaryToFormat(binaryIp);
    }

    int num = 32 - cidr;
    int allHosts = (int) Math.pow(2, num) -1;

    long startInt = Long.parseLong(binaryIp,2);
    long endInt = startInt + allHosts;
    String endBin = String.format("%32s", Integer.toBinaryString((int) endInt)).replace(' ', '0');
    String end = ipBinaryToFormat(endBin);

    return end;
  }


  //** Calculates number of available hosts from CIDR. */
  public int numberOfHosts(int cidr) {
    int num = 32 - cidr;

    double allHosts = Math.pow(2, num);
    int usableHosts = (int) allHosts;

    if(cidr >= 31) {
      return usableHosts;
    } else {
      return usableHosts - 2;
    }
  }


  //** Generates String of full IP address range. */
  public String rangeOfAddresses(String binaryIp, int cidr) {
    String start = ipBinaryToFormat(binaryIp);
    if(cidr == 32){
      return start;
    }

    int num = 32 - cidr;
    int allHosts = (int) Math.pow(2, num) - 1;

    long startInt = Long.parseLong(binaryIp,2);
    long endInt = startInt + allHosts;
    String endBin = String.format("%32s", Integer.toBinaryString((int) endInt)).replace(' ', '0');
    String end = ipBinaryToFormat(endBin);

    return start + " - " + end;
  }


  //** Generates String of usable IP address range. */
  public String usableIpAddresses(String binaryIp, int cidr) {
    if(cidr == 32){
      ipBinaryToFormat(binaryIp);
      return ipBinaryToFormat(binaryIp);
    }

    int num = 32 - cidr;
    int allHosts = (int) Math.pow(2, num) -1;

    long startInt = Long.parseLong(binaryIp,2);
    long endInt = startInt + allHosts;

    if(cidr < 31) {
      startInt++;
      endInt--;
    }

    String startBin = String.format("%32s", Integer.toBinaryString((int) startInt)).replace(' ', '0');
    String start = ipBinaryToFormat(startBin);

    String endBin = String.format("%32s", Integer.toBinaryString((int) endInt)).replace(' ', '0');
    String end = ipBinaryToFormat(endBin);

    return start + " - " + end;
  }
}
