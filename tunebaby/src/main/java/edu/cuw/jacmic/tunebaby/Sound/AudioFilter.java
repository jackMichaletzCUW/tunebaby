package edu.cuw.jacmic.tunebaby.Sound;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.*;

public class AudioFilter {

	public static byte[] extract(byte[] samples, int index, int outOf) {
		byte[] extracted = new byte[samples.length / outOf];
		
		/*int count = 0;
		
		for(int i = 0; i < samples.length; i += (2 * outOf)) {
			extracted[count++] = samples[i + (2 * (index - 1))];
			extracted[count++] = samples[i + (2 * (index - 1)) + 1];
		}*/
		int count = 0;
		
		for(int i = index - 1; i < samples.length; i += outOf) {
			extracted[count++] = samples[i];
		}
		
		return extracted;
	}
	
	public static float[] muffle(float[] samples, int intensity) {
		//float max = getMax(samples);
		float[] muffled = new float[samples.length];
		
		float outputSample = 0;
		for (int i = 0; i < samples.length; i++) {
			outputSample = (outputSample * (float)intensity + samples[i]  * (float)(100 - intensity)) / 100f;
			muffled[i] = outputSample;
		}
		
		//normalize(muffled, max);
		return muffled;
	}
	
	public static float normalize(float[] samples, float originalMax) {
		float max = getMax(samples);
				
		float multiplier = originalMax / max;
		
		for (int i = 0; i < samples.length; i++) {
			samples[i] *= multiplier;
		}
		
		return multiplier;
	}
	
	public static void gain(float[] samples, float intensity) {
		for (int i = 0; i < samples.length; i++) {
			samples[i] *= intensity;
		}
	}
	
	public static float[] reverb(float[] samples, float delayinMilliSeconds, float decayFactor, int mixPercent) {
		float omax = getMax(samples);
		//This step converts the byte array into and array of samples. This is for simplicity in data manipulation required for reverb	
		int bufferSize = samples.length;
		int sampleRate = 44100;
		/*	
		 * Second Step:
		 *	Method calls for the 4 Comb Filters in parallel. Defined at the bottom
		 */
		
		float[] combFilterSamples1 = combFilter(samples, bufferSize, delayinMilliSeconds, decayFactor, sampleRate);
		float[] combFilterSamples2 = combFilter(samples, bufferSize, (delayinMilliSeconds - 11.73f), (decayFactor - 0.1313f), sampleRate);
		float[] combFilterSamples3 = combFilter(samples, bufferSize, (delayinMilliSeconds + 19.31f), (decayFactor - 0.2743f), sampleRate);
		float[] combFilterSamples4 = combFilter(samples, bufferSize, (delayinMilliSeconds - 7.97f), (decayFactor - 0.31f), sampleRate);
		
		//Adding the 4 Comb Filters
		float[] outputComb = new float[bufferSize];
		for( int i = 0; i < bufferSize; i++) 
		{
			outputComb[i] = 0.25f * ((combFilterSamples1[i] + combFilterSamples2[i] + combFilterSamples3[i] + combFilterSamples4[i])) ;
		}	   	
	
		//Deallocating individual Comb Filter array outputs
		combFilterSamples1 = null;
		combFilterSamples2 = null;
		combFilterSamples3 = null;
		combFilterSamples4 = null;
	
		//Algorithm for Dry/Wet Mix in the output audio
		float [] mixAudio = new float[bufferSize];
		for(int i=0; i<bufferSize; i++)
			mixAudio[i] = ((100 - mixPercent) * samples[i]) + (mixPercent * outputComb[i]); 

		
		//Method calls for 2 All Pass Filters. Defined at the bottom
		float[] allPassFilterSamples1 = allPassFilter(mixAudio, bufferSize, sampleRate);
		float[] allPassFilterSamples2 = allPassFilter(allPassFilterSamples1, bufferSize, sampleRate);

		normalize(allPassFilterSamples2, omax);
		
		return allPassFilterSamples2;
	}
	

	//Method for Comb Filter
	public static float[] combFilter(float[] samples, int samplesLength, float delayinMilliSeconds, float decayFactor, float sampleRate)
	{
		//Calculating delay in samples from the delay in Milliseconds. Calculated from number of samples per millisecond
		int delaySamples = (int) ((float)delayinMilliSeconds * (sampleRate/1000));
		
		float[] combFilterSamples = Arrays.copyOf(samples, samplesLength);
	
		//Applying algorithm for Comb Filter
		for (int i=0; i<samplesLength - delaySamples; i++)
		{
			combFilterSamples[i+delaySamples] += ((float)combFilterSamples[i] * decayFactor);
		}
	return combFilterSamples;
	}
	
	//Method for All Pass Filter
	public static float[] allPassFilter(float[] samples, int samplesLength, float sampleRate)
	{
		int delaySamples = (int) ((float)89.27f * (sampleRate/1000)); // Number of delay samples. Calculated from number of samples per millisecond
		float[] allPassFilterSamples = new float[samplesLength];
		float decayFactor = 0.131f;

		//Applying algorithm for All Pass Filter
		for(int i=0; i<samplesLength; i++)
			{
			allPassFilterSamples[i] = samples[i];
		
			if(i - delaySamples >= 0)
				allPassFilterSamples[i] += -decayFactor * allPassFilterSamples[i-delaySamples];
		
			if(i - delaySamples >= 1)
				allPassFilterSamples[i] += decayFactor * allPassFilterSamples[i+20-delaySamples];
			}
		
	
		//This is for smoothing out the samples and normalizing the audio. Without implementing this, the samples overflow causing clipping of audio
		float value = allPassFilterSamples[0];
		float max = 0.0f;
		
		for(int i=0; i < samplesLength; i++)
		{
			if(Math.abs(allPassFilterSamples[i]) > max)
				max = Math.abs(allPassFilterSamples[i]);
		}
		
		for(int i=0; i<allPassFilterSamples.length; i++)
		{
			float currentValue = allPassFilterSamples[i];
			value = ((value + (currentValue - value))/max);

			allPassFilterSamples[i] = value;
		}	
	return allPassFilterSamples;
	}
	
	public static float[] getLeftChannel(float[] samples) {
		float[] leftChannel = new float[samples.length / 2];
		
		for (int i = 0; i < samples.length; i++) {
			if(i % 2 == 0) {
				leftChannel[i / 2] = samples[i];
			}
		}
		
		return leftChannel;
	}
	
	public static float[] getRightChannel(float[] samples) {
		float[] rightChannel = new float[samples.length / 2];
		
		for (int i = 0; i < samples.length; i++) {
			if(i % 2 == 1) {
				rightChannel[i / 2] = samples[i];
			}
		}
		
		return rightChannel;
	}
	
	public static float[] merge(float[] leftChannel, float[] rightChannel) {
		float[] merged = new float[leftChannel.length * 2];
		
		for (int i = 0; i < merged.length; i++) {
			if(i % 2 == 0) {
				merged[i] = leftChannel[i / 2];
			} else {
				merged[i] = rightChannel[i / 2];
			}
		}
		
		return merged;
	}
	
	public static float[] changeFloatSpeed(float[] target, double amount) {
		float[] altered = new float[(int)(target.length * amount)];
		
		double multiplier = (double)target.length / (double)(altered.length + 1);
		
		for (int i = 0; i < altered.length; i++) {
			double x = multiplier * i;
			
			int x1 = (int)x + 1;
			int x0 = (int)x;
			
			float y0 = target[x0];
			float y1 = target[x1];
			
			//double lowerMultiplier = x - lowerIndex;
			
			altered[i] = (float)( (y0 * (1 - ((x - x0) / (x1 - x0)))) + (y1 * ((x - x0) / (x1 - x0))) );

			
			//altered[i] = (float)((target[higherIndex] * higherMultiplier) + (target[lowerIndex] * lowerMultiplier));
		}
		
		return altered;
	}
	
	public static float getMax(float[] samples) {
		float max = 0;
		
		for (int i = 0; i < samples.length; i++) {
			if(Math.abs(samples[i]) > max) {
				max = Math.abs(samples[i]);
			}
		}
		
		//System.out.printf("\t%.4f\n", max);
		
		return max;
	}
	
	public static float[] changeSpeed(float[] samples, double amount) {
		float[] lc = getLeftChannel(samples);
		float[] rc = getRightChannel(samples);
		
		float lmax = getMax(lc);
		float rmax = getMax(rc);
		
		float[] laffected = changeFloatSpeed(lc, amount);
		float[] raffected = changeFloatSpeed(rc, amount);
		
		float[] affected = merge(laffected, raffected);
		return affected;
		//return samples;//merge(getLeftChannel(samples), getRightChannel(samples));
	}
	
	public static float[] addReverb(float[] samples, float delayinMilliseconds, float decayFactor, int mixPercent) {
		float[] lc = getLeftChannel(samples);
		float[] rc = getRightChannel(samples);
		
		float lmax = getMax(lc);
		float rmax = getMax(rc);
		
		float[] laffected = reverb(lc, delayinMilliseconds, decayFactor, mixPercent);
		float[] raffected = reverb(rc, delayinMilliseconds, decayFactor, mixPercent);
		
		normalize(laffected, lmax);
		normalize(raffected, rmax);
		
		float[] affected = merge(laffected, raffected);
		return affected;
		
		//float[] affected = merge(reverb(getLeftChannel(samples), delayinMilliseconds, decayFactor, mixPercent), reverb(getRightChannel(samples), delayinMilliseconds, decayFactor, mixPercent));
	}
	
	public static float[] addMuffle(float[] samples, int intensity) {
		float[] lc = getLeftChannel(samples);
		float[] rc = getRightChannel(samples);
		
		float lmax = getMax(lc);
		float rmax = getMax(rc);
		
		float[] laffected = muffle(lc, intensity);
		float[] raffected = muffle(rc, intensity);
		
		normalize(laffected, lmax);
		normalize(raffected, rmax);
		
		float[] affected = merge(laffected, raffected);
		return affected;
		
		//float[] affected = merge(muffle(getLeftChannel(samples), intensity), muffle(getRightChannel(samples), intensity));
	}
	
	public static int unpack(byte[]      bytes,
            float[]     samples,
            int         blen,
            AudioFormat audioFormat) {
			int   bitsPerSample = audioFormat.getSampleSizeInBits();
			int  bytesPerSample = bytesPerSample(bitsPerSample);
			Encoding   encoding = audioFormat.getEncoding();
			double    fullScale = fullScale(bitsPerSample);
	
    int i = 0;
    int s = 0;
    while (i < blen)
    	{
    	long temp = unpackBits(bytes, i, bytesPerSample);
        float sample = 0f;
        
        if (encoding == Encoding.PCM_SIGNED) {
            temp = extendSign(temp, bitsPerSample);
            sample = (float) (temp / fullScale);

        } else if (encoding == Encoding.PCM_UNSIGNED) {
            temp = signUnsigned(temp, bitsPerSample);
            sample = (float) (temp / fullScale);
        }
        samples[s] = sample;

        i += bytesPerSample;
        s++;
    	}
    return i;
	}
	
	/**
     * Converts: from an audio sample array to a byte array
     * 
     * Input Arguments:
     * 	samples - an array of audio samples to encode.
     *  bytes - an array to fill up with bytes.
     *  slen - the return value of 'unpack'
     *	audioFormat - the destination AudioFormat.
     * 
     * Return value:
     * 	The number of valid bytes converted.
     *	
     *	The byte array supplied is filled with the sample data converted back to bytes.
     */
	
	public static int pack(float[]     samples,
            byte[]      bytes,
            int         slen,
            AudioFormat audioFormat) {
			int   bitsPerSample = audioFormat.getSampleSizeInBits();
			int  bytesPerSample = bytesPerSample(bitsPerSample);
			Encoding   encoding = audioFormat.getEncoding();
			double    fullScale = fullScale(bitsPerSample);

			int i = 0;
			int s = 0;
			while (s < slen) {
				float sample = samples[s];
				long temp = 0L;

				if (encoding == Encoding.PCM_SIGNED) {
	                temp = (long) (sample * fullScale);

	            } else if (encoding == Encoding.PCM_UNSIGNED) {
	                temp = (long) (sample * fullScale);
	                temp = unsignSigned(temp, bitsPerSample);
	            }   
				
	            packBits(bytes, i, temp, bytesPerSample);

				i += bytesPerSample;
				s++;
			}
			return i;
	}
	
    //	This is done for the PCM-Signed encoding. 
	//	The calling method is converting the byte data into long. So the twos-complement sign must be extended.
	//	There are 64 bits per long. So the bits in the sample are first shifted to the left and then the right-shift will do the filling.
    public static long extendSign(long temp, int bitsPerSample) {
        int extensionBits = 64 - bitsPerSample;
        return (temp << extensionBits) >> extensionBits;
    }
    
    //	Computes the largest magnitude representable by the audio format,
    //	with pow(2.0, bitsPerSample - 1).
    //
    //This is used for scaling the float array to the -1.0f to 1.0f range
    public static double fullScale(int bitsPerSample) {
        return Math.pow(2.0, bitsPerSample - 1);
    }
    
    //	The UnSigned values are converted to Signed values.
    //	UnSigned values are simply offset such that the 'fullScale' corresponds to zero value
    //	So subtract the fullScale from the value of the sample and later scale it.
    private static long signUnsigned(long temp, int bitsPerSample) {
        return temp - (long) fullScale(bitsPerSample);
    }

	//	Computes the block-aligned bytes per sample of the audio format,
    //	with {(int) ceil(bitsPerSample / 8.0)}.

    //	Round towards the ceiling because formats that allow bit depths
    //	in non-integral multiples of 8 typically pad up to the nearest
    //	integral multiple of 8.

    public static int bytesPerSample(int bitsPerSample) {
        return (int) Math.ceil(bitsPerSample / 8.0);
    }
            
    private static long unpackBits(byte[]  bytes,
            int     i,
            int     bytesPerSample) {
    	switch (bytesPerSample) {
    		case  1: return unpack8Bit(bytes, i);
    		case  2: return unpack16Bit(bytes, i);
    		default: return 1;
    		}
    }
    
    /*
     * 	The byte array contains the sample frames split up and all in a line. 
     * 	The WAV files are encoded in little-endian, the least significant byte is earlier in the order.
     * 
     * 	Bitwise AND each byte with the mask 0xFF (which is 0b1111_1111) to avoid sign extension when the byte is automatically promoted
     */
    
    //	This method converts the byte data into a long
    //	When the data is stored in 8-bit encoding, the conversion is straightforward. Each element in byte array corresponds to each sample.
    private static long unpack8Bit(byte[] bytes, int i) {
    	return bytes[i] & 0xffL;
    }

    //	This method converts the byte data into a long
    //	When the data is stored in 16-bit encoding, the bytes need to be bit shifted into position, and Bitwise OR to put the bytes together.
    private static long unpack16Bit(byte[]  bytes,
             int     i) {
    		return ((bytes[i    ] & 0xffL)
    				| ((bytes[i + 1] & 0xffL) << 8L));
    			}
    
    private static void packBits(byte[]  bytes,
            int     i,
            long    temp,
            int     bytesPerSample) {
    	switch (bytesPerSample) {
    		case  1: pack8Bit(bytes, i, temp);
    			break;
    		case  2: pack16Bit(bytes, i, temp);
    			break;
    		default: ;
    			break;
    	}
    }
    
    /*
     * 	Following methods just reverse the processing done for the 'unpack' method to get back
     * 	a byte array from float array.
     */
    
    private static void pack8Bit(byte[] bytes, int i, long temp) {
		bytes[i] = (byte) (temp & 0xffL);
    }

    private static void pack16Bit(byte[]  bytes,
             int     i,
             long    temp)  {
            bytes[i    ] = (byte) ( temp         & 0xffL);
            bytes[i + 1] = (byte) ((temp >>> 8L) & 0xffL);
        }
    
    private static long unsignSigned(long temp, int bitsPerSample) {
        return temp + (long) fullScale(bitsPerSample);
    }
	
}
