/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.pdf.encoding;

import de.intarsys.pdf.cos.COSObject;

/**
 * Implementation of MacRomanEncoding
 */
public class MacRomanEncoding extends MappedEncoding {
    public static MacRomanEncoding UNIQUE = new MacRomanEncoding();

    protected MacRomanEncoding() {
        super();
        initialize();
    }

    @Override
    public COSObject cosGetObject() {
        return CN_MacRomanEncoding;
    }

    @Override
    public String getName() {
        return "MacRomanEncoding";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.MappedEncoding#initialize()
     */
    protected void initialize() {
        addEncoding(0101, Encoding.NAME_A);
        addEncoding(0256, Encoding.NAME_AE);
        addEncoding(0347, Encoding.NAME_Aacute);
        addEncoding(0345, Encoding.NAME_Acircumflex);
        addEncoding(0200, Encoding.NAME_Adieresis);
        addEncoding(0313, Encoding.NAME_Agrave);
        addEncoding(0201, Encoding.NAME_Aring);
        addEncoding(0314, Encoding.NAME_Atilde);
        addEncoding(0102, Encoding.NAME_B);
        addEncoding(0103, Encoding.NAME_C);
        addEncoding(0202, Encoding.NAME_Ccedilla);
        addEncoding(0104, Encoding.NAME_D);
        addEncoding(0105, Encoding.NAME_E);
        addEncoding(0203, Encoding.NAME_Eacute);
        addEncoding(0346, Encoding.NAME_Ecircumflex);
        addEncoding(0350, Encoding.NAME_Edieresis);
        addEncoding(0351, Encoding.NAME_Egrave);
        // addEncoding(0320,Encoding.NAME_Eth);
        // addEncoding(0240,Encoding.NAME_Euro);
        addEncoding(0106, Encoding.NAME_F);
        addEncoding(0107, Encoding.NAME_G);
        addEncoding(0110, Encoding.NAME_H);
        addEncoding(0111, Encoding.NAME_I);
        addEncoding(0352, Encoding.NAME_Iacute);
        addEncoding(0353, Encoding.NAME_Icircumflex);
        addEncoding(0354, Encoding.NAME_Idieresis);
        addEncoding(0355, Encoding.NAME_Igrave);
        addEncoding(0112, Encoding.NAME_J);
        addEncoding(0113, Encoding.NAME_K);
        addEncoding(0114, Encoding.NAME_L);
        // addEncoding(0225,Encoding.NAME_Lslash);
        addEncoding(0115, Encoding.NAME_M);
        addEncoding(0116, Encoding.NAME_N);
        addEncoding(0204, Encoding.NAME_Ntilde);
        addEncoding(0117, Encoding.NAME_O);
        addEncoding(0316, Encoding.NAME_OE);
        addEncoding(0356, Encoding.NAME_Oacute);
        addEncoding(0357, Encoding.NAME_Ocircumflex);
        addEncoding(0205, Encoding.NAME_Odieresis);
        addEncoding(0361, Encoding.NAME_Ograve);
        addEncoding(0257, Encoding.NAME_Oslash);
        addEncoding(0315, Encoding.NAME_Otilde);
        addEncoding(0120, Encoding.NAME_P);
        addEncoding(0121, Encoding.NAME_Q);
        addEncoding(0122, Encoding.NAME_R);
        addEncoding(0123, Encoding.NAME_S);
        // addEncoding(0227,Encoding.NAME_Scaron);
        addEncoding(0124, Encoding.NAME_T);
        // addEncoding(0336,Encoding.NAME_Thorn);
        addEncoding(0125, Encoding.NAME_U);
        addEncoding(0362, Encoding.NAME_Uacute);
        addEncoding(0363, Encoding.NAME_Ucircumflex);
        addEncoding(0206, Encoding.NAME_Udieresis);
        addEncoding(0364, Encoding.NAME_Ugrave);
        addEncoding(0126, Encoding.NAME_V);
        addEncoding(0127, Encoding.NAME_W);
        addEncoding(0130, Encoding.NAME_X);
        addEncoding(0131, Encoding.NAME_Y);
        // addEncoding(0335,Encoding.NAME_Yacute);
        addEncoding(0331, Encoding.NAME_Ydieresis);
        addEncoding(0132, Encoding.NAME_Z);
        // addEncoding(0231,Encoding.NAME_Zcaron);
        addEncoding(0141, Encoding.NAME_a);
        addEncoding(0207, Encoding.NAME_aacute);
        addEncoding(0211, Encoding.NAME_acircumflex);
        addEncoding(0253, Encoding.NAME_acute);
        addEncoding(0212, Encoding.NAME_adieresis);
        addEncoding(0276, Encoding.NAME_ae);
        addEncoding(0210, Encoding.NAME_agrave);
        addEncoding(0046, Encoding.NAME_ampersand);
        addEncoding(0214, Encoding.NAME_aring);
        addEncoding(0136, Encoding.NAME_asciicircum);
        addEncoding(0176, Encoding.NAME_asciitilde);
        addEncoding(0052, Encoding.NAME_asterisk);
        addEncoding(0100, Encoding.NAME_at);
        addEncoding(0213, Encoding.NAME_atilde);
        addEncoding(0142, Encoding.NAME_b);
        addEncoding(0134, Encoding.NAME_backslash);
        addEncoding(0174, Encoding.NAME_bar);
        addEncoding(0173, Encoding.NAME_braceleft);
        addEncoding(0175, Encoding.NAME_braceright);
        addEncoding(0133, Encoding.NAME_bracketleft);
        addEncoding(0135, Encoding.NAME_bracketright);
        addEncoding(0371, Encoding.NAME_breve);
        // addEncoding(0246,Encoding.NAME_brokenbar);
        addEncoding(0245, Encoding.NAME_bullet);
        addEncoding(0143, Encoding.NAME_c);
        addEncoding(0377, Encoding.NAME_caron);
        addEncoding(0215, Encoding.NAME_ccedilla);
        addEncoding(0374, Encoding.NAME_cedilla);
        addEncoding(0242, Encoding.NAME_cent);
        addEncoding(0366, Encoding.NAME_circumflex);
        addEncoding(0072, Encoding.NAME_colon);
        addEncoding(0054, Encoding.NAME_comma);
        addEncoding(0251, Encoding.NAME_copyright);
        addEncoding(0333, Encoding.NAME_currency);
        addEncoding(0144, Encoding.NAME_d);
        addEncoding(0240, Encoding.NAME_dagger);
        addEncoding(0340, Encoding.NAME_daggerdbl);
        addEncoding(0241, Encoding.NAME_degree);
        addEncoding(0254, Encoding.NAME_dieresis);
        addEncoding(0326, Encoding.NAME_divide);
        addEncoding(0044, Encoding.NAME_dollar);
        addEncoding(0372, Encoding.NAME_dotaccent);
        addEncoding(0365, Encoding.NAME_dotlessi);
        addEncoding(0145, Encoding.NAME_e);
        addEncoding(0216, Encoding.NAME_eacute);
        addEncoding(0220, Encoding.NAME_ecircumflex);
        addEncoding(0221, Encoding.NAME_edieresis);
        addEncoding(0217, Encoding.NAME_egrave);
        addEncoding(0070, Encoding.NAME_eight);
        addEncoding(0311, Encoding.NAME_ellipsis);
        addEncoding(0321, Encoding.NAME_emdash);
        addEncoding(0320, Encoding.NAME_endash);
        addEncoding(0075, Encoding.NAME_equal);
        // addEncoding(0360,Encoding.NAME_eth);
        addEncoding(0041, Encoding.NAME_exclam);
        addEncoding(0301, Encoding.NAME_exclamdown);
        addEncoding(0146, Encoding.NAME_f);
        addEncoding(0336, Encoding.NAME_fi);
        addEncoding(0065, Encoding.NAME_five);
        addEncoding(0337, Encoding.NAME_fl);
        addEncoding(0304, Encoding.NAME_florin);
        addEncoding(0064, Encoding.NAME_four);
        addEncoding(0332, Encoding.NAME_fraction);
        addEncoding(0147, Encoding.NAME_g);
        addEncoding(0247, Encoding.NAME_germandbls);
        addEncoding(0140, Encoding.NAME_grave);
        addEncoding(0076, Encoding.NAME_greater);
        addEncoding(0307, Encoding.NAME_guillemotleft);
        addEncoding(0310, Encoding.NAME_guillemotright);
        addEncoding(0334, Encoding.NAME_guilsinglleft);
        addEncoding(0335, Encoding.NAME_guilsinglright);
        addEncoding(0150, Encoding.NAME_h);
        addEncoding(0375, Encoding.NAME_hungarumlaut);
        addEncoding(0055, Encoding.NAME_hyphen);
        addEncoding(0151, Encoding.NAME_i);
        addEncoding(0222, Encoding.NAME_iacute);
        addEncoding(0224, Encoding.NAME_icircumflex);
        addEncoding(0225, Encoding.NAME_idieresis);
        addEncoding(0223, Encoding.NAME_igrave);
        addEncoding(0152, Encoding.NAME_j);
        addEncoding(0153, Encoding.NAME_k);
        addEncoding(0154, Encoding.NAME_l);
        addEncoding(0074, Encoding.NAME_less);
        addEncoding(0302, Encoding.NAME_logicalnot);
        // addEncoding(0233,Encoding.NAME_lslash);
        addEncoding(0155, Encoding.NAME_m);
        addEncoding(0370, Encoding.NAME_macron);
        // addEncoding(0212,Encoding.NAME_minus);
        addEncoding(0265, Encoding.NAME_mu);
        // addEncoding(0327,Encoding.NAME_multiply);
        addEncoding(0156, Encoding.NAME_n);
        addEncoding(0071, Encoding.NAME_nine);
        addEncoding(0226, Encoding.NAME_ntilde);
        addEncoding(0043, Encoding.NAME_numbersign);
        addEncoding(0157, Encoding.NAME_o);
        addEncoding(0227, Encoding.NAME_oacute);
        addEncoding(0231, Encoding.NAME_ocircumflex);
        addEncoding(0232, Encoding.NAME_odieresis);
        addEncoding(0317, Encoding.NAME_oe);
        addEncoding(0376, Encoding.NAME_ogonek);
        addEncoding(0230, Encoding.NAME_ograve);
        addEncoding(0061, Encoding.NAME_one);
        // addEncoding(0275,Encoding.NAME_onehalf);
        // addEncoding(0274,Encoding.NAME_onequarter);
        // addEncoding(0271,Encoding.NAME_onesuperior);
        addEncoding(0273, Encoding.NAME_ordfeminine);
        addEncoding(0274, Encoding.NAME_ordmasculine);
        addEncoding(0277, Encoding.NAME_oslash);
        addEncoding(0233, Encoding.NAME_otilde);
        addEncoding(0160, Encoding.NAME_p);
        addEncoding(0246, Encoding.NAME_paragraph);
        addEncoding(0050, Encoding.NAME_parenleft);
        addEncoding(0051, Encoding.NAME_parenright);
        addEncoding(0045, Encoding.NAME_percent);
        addEncoding(0056, Encoding.NAME_period);
        addEncoding(0341, Encoding.NAME_periodcentered);
        addEncoding(0344, Encoding.NAME_perthousand);
        addEncoding(0053, Encoding.NAME_plus);
        addEncoding(0261, Encoding.NAME_plusminus);
        addEncoding(0161, Encoding.NAME_q);
        addEncoding(0077, Encoding.NAME_question);
        addEncoding(0300, Encoding.NAME_questiondown);
        addEncoding(0042, Encoding.NAME_quotedbl);
        addEncoding(0343, Encoding.NAME_quotedblbase);
        addEncoding(0322, Encoding.NAME_quotedblleft);
        addEncoding(0323, Encoding.NAME_quotedblright);
        addEncoding(0324, Encoding.NAME_quoteleft);
        addEncoding(0325, Encoding.NAME_quoteright);
        addEncoding(0342, Encoding.NAME_quotesinglbase);
        addEncoding(0047, Encoding.NAME_quotesingle);
        addEncoding(0162, Encoding.NAME_r);
        addEncoding(0250, Encoding.NAME_registered);
        addEncoding(0373, Encoding.NAME_ring);
        addEncoding(0163, Encoding.NAME_s);
        // addEncoding(0235,Encoding.NAME_scaron);
        addEncoding(0244, Encoding.NAME_section);
        addEncoding(0073, Encoding.NAME_semicolon);
        addEncoding(0067, Encoding.NAME_seven);
        addEncoding(0066, Encoding.NAME_six);
        addEncoding(0057, Encoding.NAME_slash);
        // alternative: nonbreaking space; add first so reverse mapping will be
        // overwritten by proper value later
        addEncoding(0312, Encoding.NAME_space);
        addEncoding(0040, Encoding.NAME_space);
        addEncoding(0243, Encoding.NAME_sterling);
        addEncoding(0164, Encoding.NAME_t);
        // addEncoding(0376,Encoding.NAME_thorn);
        addEncoding(0063, Encoding.NAME_three);
        // addEncoding(0276,Encoding.NAME_threequarters);
        // addEncoding(0263,Encoding.NAME_threesuperior);
        addEncoding(0367, Encoding.NAME_tilde);
        addEncoding(0252, Encoding.NAME_trademark);
        addEncoding(0062, Encoding.NAME_two);
        // addEncoding(0262,Encoding.NAME_twosuperior);
        addEncoding(0165, Encoding.NAME_u);
        addEncoding(0234, Encoding.NAME_uacute);
        addEncoding(0236, Encoding.NAME_ucircumflex);
        addEncoding(0237, Encoding.NAME_udieresis);
        addEncoding(0235, Encoding.NAME_ugrave);
        addEncoding(0137, Encoding.NAME_underscore);
        addEncoding(0166, Encoding.NAME_v);
        addEncoding(0167, Encoding.NAME_w);
        addEncoding(0170, Encoding.NAME_x);
        addEncoding(0171, Encoding.NAME_y);
        // addEncoding(0375,Encoding.NAME_yacute);
        addEncoding(0330, Encoding.NAME_ydieresis);
        addEncoding(0264, Encoding.NAME_yen);
        addEncoding(0172, Encoding.NAME_z);
        // addEncoding(0236,Encoding.NAME_zcaron);
        addEncoding(0060, Encoding.NAME_zero);
    }
}
