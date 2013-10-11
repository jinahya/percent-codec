/*
 *  Copyright 2010 Jin Kwon.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package com.github.jinahya.codec;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * Percent Encoder.
 *
 * @author <a href="mailto:jinahya@gmail.com">Jin Kwon</a>
 * @see <a href="http://goo.gl/w4GhD">Percent Encoding</a>
 */
public class PercentEncoder {


//    private static int encodeHalf(final int decoded) {
//
//        switch (decoded) {
//            case 0x00:
//            case 0x01:
//            case 0x02:
//            case 0x03:
//            case 0x04:
//            case 0x05:
//            case 0x06:
//            case 0x07:
//            case 0x08:
//            case 0x09:
//                return decoded + 0x30; // 0x30('0') - 0x39('9')
//            case 0x0A:
//            case 0x0B:
//            case 0x0C:
//            case 0x0D:
//            case 0x0E:
//            case 0x0F:
//                return decoded + 0x37; // 0x41('A') - 0x46('F')
//            default:
//                throw new IllegalArgumentException("illegal half: " + decoded);
//        }
//    }
    public static void encodeSingle(final int input, final byte[] output,
                                    final int outoff) {

        if (output == null) {
            throw new NullPointerException("output");
        }

        if (output.length < 1) {
            // not required by (outoff >= output.length) check below
            throw new IllegalArgumentException(
                "encoded.length(" + output.length + ") < 1");
        }

        if (outoff < 0) {
            throw new IllegalArgumentException("outoff(" + outoff + ") < 0");
        }

        if (outoff >= output.length) {
            throw new IllegalArgumentException(
                "outoff(" + outoff + ") >= output.length(" + output.length
                + ")");
        }

        if ((input >= 0x30 && input <= 0x39) // digit
            || (input >= 0x41 && input <= 0x5A) // upper case alpha
            || (input >= 0x61 && input <= 0x7A) // lower case alpha
            || input == 0x2D // '-'
            || input == 0x5F // '_'
            || input == 0x2E // '.'
            || input == 0x7E) { // '~'
            output[outoff] = (byte) input;
        } else {
            if (outoff >= output.length - 2) {
                throw new IllegalArgumentException(
                    "outoff(" + outoff + ") >= output.length(" + output.length
                    + ") - 2");
            }
            output[outoff] = 0x25; // '%'
            HexEncoder.encodeSingle(input, output, outoff + 1);
            //output[outoff + 1] = (byte) encodeHalf((input >> 4) & 0xFF);
            //output[outoff + 2] = (byte) encodeHalf(input & 0x0F);
        }
    }


    /**
     *
     * @param input the input byte array
     *
     * @return encoded output
     */
    public static byte[] encodeMultiple(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        final byte[] output = new byte[input.length * 3]; // possible maximum
        int outoff = 0;

        for (int i = 0; i < input.length; i++) {
            encodeSingle(input[i] & 0xFF, output, outoff);
            outoff += (output[outoff] == 0x25 ? 3 : 1);
        }

        if (outoff == output.length) {
            return output;
        }

        final byte[] trimmed = new byte[outoff];
        System.arraycopy(output, 0, trimmed, 0, outoff);

        return trimmed;
    }


    /**
     * Encodes given bytes.
     *
     * @param input bytes to encode
     *
     * @return encoded output
     */
    public byte[] encode(final byte[] input) {

        return encodeMultiple(input);
    }


    public String encodeToString(final byte[] input, final String outputCharset)
        throws UnsupportedEncodingException {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset");
        }

        return new String(encode(input), outputCharset);
    }


    public String encodeToString(final byte[] input,
                                 final Charset outputCharset) {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset");
        }

        return new String(encode(input), outputCharset);
    }


    /**
     * Encodes given {@code input}.
     *
     * @param input string to encode
     * @param input charset name
     *
     * @return encoded output
     *
     * @throws UnsupportedEncodingException if specified {@code inputCharset} is
     * not supported
     */
    public byte[] encode(final String input, final String inputCharset)
        throws UnsupportedEncodingException {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if (inputCharset == null) {
            throw new NullPointerException("inputCharset");
        }

        return encode(input.getBytes(inputCharset));
    }


    public byte[] encode(final String input, final Charset inputCharset) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if (inputCharset == null) {
            throw new NullPointerException("inputCharset");
        }

        return encode(input.getBytes(inputCharset));
    }


    public String encodeToString(final String input, final String inputCharset,
                                 final String outputCharset)
        throws UnsupportedEncodingException {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset");
        }

        return new String(encode(input, inputCharset), outputCharset);
    }


    public String encodeToString(final String input, final Charset inputCharset,
                                 final Charset outputCharset) {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset");
        }

        return new String(encode(input, inputCharset), outputCharset);
    }


}
