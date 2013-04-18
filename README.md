android_network_inspect_library
===============================

Simple library to help inspecting your network data

to use this library
        
copy the tcpdump.jpeg file in your assets app folder
add 
                <service android:name="eu.codlab.network.inspect.library.DumpService" />
        in your <application></application> to enable the service starting
        
and start the service of course, you can directly use the TcpDumpManager
        
This library is absolutely not finished, but i wanted to release an pre-alpha version to set up the repository etc...

It currently use root rights

but I am working on a non root inspection (but which will be more 'battery consummer' as an alarm lopp will be implemented to inspect sys class)