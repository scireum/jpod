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
/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
package de.intarsys.pdf.postscript;

public interface ParserConstants {
	int DECIMAL_LITERAL = 4;

	int DEFAULT = 0;

	int DIGIT = 13;

	int EOF = 0;

	int EXPONENT = 7;

	int FLOATING_POINT_LITERAL = 6;

	int HEX_LITERAL = 5;

	int IDENTIFIER = 9;

	int IMMEDIATE_IDENTIFIER = 11;

	int INTEGER_LITERAL = 3;

	int KEY_IDENTIFIER = 10;

	int LBRACE = 14;

	int LBRACKET = 16;

	int LETTER = 12;

	int RBRACE = 15;

	int RBRACKET = 17;

	int STRING_LITERAL = 8;

	String[] tokenImage = { "<EOF>", "<token of kind 1>", "<token of kind 2>",
			"<INTEGER_LITERAL>", "<DECIMAL_LITERAL>", "<HEX_LITERAL>",
			"<FLOATING_POINT_LITERAL>", "<EXPONENT>", "<STRING_LITERAL>",
			"<IDENTIFIER>", "<KEY_IDENTIFIER>", "<IMMEDIATE_IDENTIFIER>",
			"<LETTER>", "<DIGIT>", "\"{\"", "\"}\"", "\"[\"", "\"]\"", };
}
