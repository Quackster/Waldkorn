package com.blunk.security;

/**
 * For now, we'll use a Static implementation of the RC4 Algorithm. USAGE: For use with
 * "SECRET_KEY 0" only!
 * 
 * @author Mike
 * @deprecated
 */
@Deprecated
public class HabboRC4_static
{
	private int i;
	private int j;
	private int table[];
	private String[] di = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	
	/**
	 * The constructor
	 * 
	 * @deprecated
	 */
	@Deprecated
	public HabboRC4_static()
	{
		// No key to init remember! As this IS STATIC!
		this.table = new int[256];
		this.i = 0;
		this.j = 0;
		
		this.init();
	}
	
	/**
	 * Sets up the encryption tables!
	 * 
	 * @deprecated
	 */
	@Deprecated
	private void init()
	{
		// No decoded key to pass remember!
		table[0] = 100;
		table[1] = 196;
		table[2] = 80;
		table[3] = 73;
		table[4] = 246;
		table[5] = 166;
		table[6] = 191;
		table[7] = 151;
		table[8] = 23;
		table[9] = 25;
		table[10] = 19;
		table[11] = 4;
		table[12] = 113;
		table[13] = 27;
		table[14] = 43;
		table[15] = 251;
		table[16] = 161;
		table[17] = 54;
		table[18] = 58;
		table[19] = 121;
		table[20] = 21;
		table[21] = 31;
		table[22] = 175;
		table[23] = 176;
		table[24] = 211;
		table[25] = 125;
		table[26] = 39;
		table[27] = 98;
		table[28] = 112;
		table[29] = 193;
		table[30] = 104;
		table[31] = 95;
		table[32] = 68;
		table[33] = 186;
		table[34] = 71;
		table[35] = 149;
		table[36] = 119;
		table[37] = 3;
		table[38] = 146;
		table[39] = 135;
		table[40] = 108;
		table[41] = 67;
		table[42] = 44;
		table[43] = 172;
		table[44] = 222;
		table[45] = 209;
		table[46] = 26;
		table[47] = 160;
		table[48] = 6;
		table[49] = 148;
		table[50] = 156;
		table[51] = 48;
		table[52] = 229;
		table[53] = 192;
		table[54] = 134;
		table[55] = 59;
		table[56] = 133;
		table[57] = 35;
		table[58] = 89;
		table[59] = 225;
		table[60] = 83;
		table[61] = 111;
		table[62] = 143;
		table[63] = 9;
		table[64] = 199;
		table[65] = 153;
		table[66] = 181;
		table[67] = 106;
		table[68] = 74;
		table[69] = 194;
		table[70] = 157;
		table[71] = 174;
		table[72] = 214;
		table[73] = 63;
		table[74] = 207;
		table[75] = 77;
		table[76] = 197;
		table[77] = 105;
		table[78] = 69;
		table[79] = 227;
		table[80] = 250;
		table[81] = 123;
		table[82] = 116;
		table[83] = 30;
		table[84] = 204;
		table[85] = 178;
		table[86] = 107;
		table[87] = 16;
		table[88] = 202;
		table[89] = 15;
		table[90] = 201;
		table[91] = 252;
		table[92] = 1;
		table[93] = 217;
		table[94] = 255;
		table[95] = 124;
		table[96] = 136;
		table[97] = 127;
		table[98] = 72;
		table[99] = 20;
		table[100] = 131;
		table[101] = 28;
		table[102] = 102;
		table[103] = 103;
		table[104] = 76;
		table[105] = 210;
		table[106] = 34;
		table[107] = 224;
		table[108] = 142;
		table[109] = 185;
		table[110] = 247;
		table[111] = 190;
		table[112] = 14;
		table[113] = 248;
		table[114] = 218;
		table[115] = 115;
		table[116] = 17;
		table[117] = 198;
		table[118] = 245;
		table[119] = 36;
		table[120] = 188;
		table[121] = 150;
		table[122] = 45;
		table[123] = 213;
		table[124] = 57;
		table[125] = 212;
		table[126] = 242;
		table[127] = 114;
		table[128] = 237;
		table[129] = 203;
		table[130] = 130;
		table[131] = 163;
		table[132] = 61;
		table[133] = 239;
		table[134] = 66;
		table[135] = 52;
		table[136] = 141;
		table[137] = 144;
		table[138] = 7;
		table[139] = 235;
		table[140] = 13;
		table[141] = 183;
		table[142] = 117;
		table[143] = 79;
		table[144] = 200;
		table[145] = 171;
		table[146] = 118;
		table[147] = 75;
		table[148] = 208;
		table[149] = 177;
		table[150] = 220;
		table[151] = 51;
		table[152] = 158;
		table[153] = 169;
		table[154] = 0;
		table[155] = 147;
		table[156] = 55;
		table[157] = 206;
		table[158] = 244;
		table[159] = 219;
		table[160] = 33;
		table[161] = 145;
		table[162] = 173;
		table[163] = 129;
		table[164] = 24;
		table[165] = 253;
		table[166] = 231;
		table[167] = 40;
		table[168] = 128;
		table[169] = 60;
		table[170] = 152;
		table[171] = 37;
		table[172] = 101;
		table[173] = 5;
		table[174] = 92;
		table[175] = 91;
		table[176] = 97;
		table[177] = 139;
		table[178] = 164;
		table[179] = 243;
		table[180] = 86;
		table[181] = 221;
		table[182] = 159;
		table[183] = 42;
		table[184] = 65;
		table[185] = 109;
		table[186] = 140;
		table[187] = 195;
		table[188] = 230;
		table[189] = 236;
		table[190] = 238;
		table[191] = 81;
		table[192] = 228;
		table[193] = 249;
		table[194] = 12;
		table[195] = 187;
		table[196] = 234;
		table[197] = 137;
		table[198] = 56;
		table[199] = 32;
		table[200] = 184;
		table[201] = 162;
		table[202] = 165;
		table[203] = 254;
		table[204] = 99;
		table[205] = 179;
		table[206] = 233;
		table[207] = 85;
		table[208] = 49;
		table[209] = 232;
		table[210] = 126;
		table[211] = 88;
		table[212] = 41;
		table[213] = 82;
		table[214] = 240;
		table[215] = 70;
		table[216] = 223;
		table[217] = 93;
		table[218] = 50;
		table[219] = 90;
		table[220] = 182;
		table[221] = 170;
		table[222] = 22;
		table[223] = 122;
		table[224] = 241;
		table[225] = 226;
		table[226] = 2;
		table[227] = 10;
		table[228] = 8;
		table[229] = 154;
		table[230] = 47;
		table[231] = 53;
		table[232] = 96;
		table[233] = 62;
		table[234] = 216;
		table[235] = 180;
		table[236] = 29;
		table[237] = 94;
		table[238] = 215;
		table[239] = 168;
		table[240] = 138;
		table[241] = 189;
		table[242] = 18;
		table[243] = 87;
		table[244] = 38;
		table[245] = 120;
		table[246] = 84;
		table[247] = 11;
		table[248] = 155;
		table[249] = 205;
		table[250] = 110;
		table[251] = 78;
		table[252] = 64;
		table[253] = 132;
		table[254] = 167;
		table[255] = 46;
	}
	
	/**
	 * Deciphers incomming Habbo Packets!
	 * 
	 * @param tData Data to decipher
	 * @return cipher Deciphered data!
	 * @deprecated
	 */
	@Deprecated
	public String decipher(String tData)
	{
		int t = 0;
		int k = 0;
		StringBuffer cipher = new StringBuffer(tData.length());
		
		for (int x = 0; x < tData.length(); x += 2)
		{
			i = (i + 1) % 256;
			j = (j + table[i]) % 256;
			t = table[i];
			table[i] = table[j];
			table[j] = t;
			k = table[(table[i] + table[j]) % 256];
			t = Integer.parseInt(tData.substring(x, x + 2), 16);
			cipher = cipher.append((char)(t ^ k));
		}
		
		return cipher.toString();
	}
	
	/**
	 * Enciphers the data.
	 * 
	 * @param tData The data to encipher
	 * @return The Enciphered data
	 * @deprecated
	 */
	@Deprecated
	public String encipher(String tData)
	{
		int t = 0;
		int k = 0;
		StringBuffer cipher = new StringBuffer(tData.length() * 2);
		
		for (int x = 0; x < tData.length(); x++)
		{
			i = (i + 1) % 256;
			j = (j + table[i]) % 256;
			t = table[i];
			table[i] = table[j];
			table[j] = t;
			k = table[(table[i] + table[j]) % 256];
			
			int c = tData.charAt(x) ^ k;
			
			if (c <= 0)
			{
				cipher.append("00");
			}
			else
			{
				cipher.append(di[c >> 4 & 0xf]);
				cipher.append(di[c & 0xf]);
			}
		}
		
		return cipher.toString();
	}
}
