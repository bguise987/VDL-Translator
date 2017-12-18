# VDL-Translator
Desktop software that translates data collected by the Vibration Data Logger at HERL.

Please note that this is old code, so while I would absolutely change it if I still used it, I can't justify the time (also I don't currently own a copy of the device, so that makes testing any modifications tough).

That said, I'm still pretty proud of it. At the time we had a few scattered copies of C# and MATLAB code and it was unclear which copies worked or were even accurate. The most complete copy was in MATLAB and took 8+ hours to translate a data set on a Xeon workstation (in fact, it took so long we killed it). This Java code translated the same data set on a 2010 Macbook Pro in under 3 minutes.
Fun fact: Why did the MATLAB version take so long? This code requires lots of bitwise operations due to how the data logger stored its data.

Some possible improvements:
- Better separate the GUI and processing code. Currently these are too intermingled.
- Make it easy to save and load a configuration file to change the equations used to convert the voltage values. This would make it easier to use different sensors with the same hardware design.
- Commenting and formatting can usually be improved on any codebase. &nbsp;&nbsp;&nbsp; :)
- Currently the main thread polls the worker threads to see if they're done. It would be much nicer if the worker threads could report back and terminate.
