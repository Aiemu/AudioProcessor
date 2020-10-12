import wave
import sys
import matplotlib.pyplot as pl 
import numpy as np 

f = wave.open(sys.argv[1], "rb") 

# Get params (nchannels, sampwidth, framerate, nframes, comptype, compname) 
params = f.getparams() 
nchannels, sampwidth, framerate, nframes = params[:4] 
print('channels:', nchannels, '\nsample width:', sampwidth, '\nframerate:', framerate, '\nframes:', nframes, '\n')

# Read in 
str_data = f.readframes(nframes) 
f.close() 

# Trans into list 
wave_data = np.fromstring(str_data, dtype=np.short) 

if nchannels == 2: # Double channel
    wave_data.shape = -1, 2 
    wave_data = wave_data.T 
    time = np.arange(0, nframes) * (1.0 / framerate) 

    # Channel 1 
    pl.subplot(211) 
    pl.plot(time, wave_data[0]) 

    # Channel 2
    pl.subplot(212) 
    pl.plot(time, wave_data[1], c="g") 

    pl.xlabel("time (seconds)") 
    pl.show()

elif nchannels == 1: # Single channel
    wave_data.shape = -1, 1 
    wave_data = wave_data.T 
    time = np.arange(0, nframes) * (1.0 / framerate) 

    # Channel 1 
    pl.subplot(211) 
    pl.plot(time, wave_data[0]) 

    pl.xlabel("time (seconds)") 
    pl.show()
