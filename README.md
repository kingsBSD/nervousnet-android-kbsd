### Welcome to Nervousnet GitHub Page.
The Nervousnet is a large-scale distributed research platform that provides real-time social sensing services as a public good. Existing Big Data systems threaten social cohesion as they are designed to be closed, proprietary, privacy-intrusive and discriminatory. In contrast, the Planetary Nervous System is an open, privacy-preserving and participatory platform designed to be collectively built by citizens and for citizens.

The Nervousnet is enabled by Internet of Things technologies and aims at seamlessly interconnecting a large number of different pervasive devices, e.g. mobile phones, smart sensors, etc. For this purpose, several universal state-of-the-art protocols and communication means are introduced. A novel social sensing paradigm shift is engineered: Users are provided with freedom and incentives to share, collect and, at the same time, protect data of their digital environment in real-time. In this way, social sensing turns into a knowledge extraction service of public good.

The social sensing services of the planetary nervous system can be publicly used for building novel innovative applications. Whether you would like to detect an earthquake, perform a secure evacuation or discover the hot spots of a visited city, the Nervousnet makes this possible by collectively sensing social activity of participatory citizens.

### Research<br>
The nervousnet project and related services is based on the research mentioned below:<br>
E. Pournaras, I. Moise and D. Helbing,<br>
<b><i>Privacy-preserving Ubiquitous Social Mining via Modular and Compositional Virtual Sensors </b></i><br>
in the proceedings of the 29th IEEE International Conference on Advanced Information Networking and Applications-AINA-2015, pages 332-338, Gwangju, South Korea, March 2015. © IEEE<br>

https://github.com/nervousnet/nervousnet-android/blob/master/Documents/Research_Paper_nervousnet.pdf



### Architecture<br>
The Android nervousnet mobile application is based on the concept of Bound Services and Android Interface Definition
Language (AIDL).<br>
<i>“A Service is an application component that can perform long-running operations in the background and does not provide a user
interface. Another application component can start a service and it will continue to run in the background even if the user switches to
another application. Additionally, a component can bind to a service to interact with it and even perform interprocess communication
(IPC).” </i><br>
https://developer.android.com/guide/components/services.html<br><br>
<i>“A bound service offers a client-server interface that allows components to interact with the service, send requests, get results, and even do so across processes with inter-process communication (IPC).”</i><br>
https://developer.android.com/guide/components/bound-services.html<br><br>
<i>“ AIDL allows you to define the programming interface that both the client and service agree upon in order to communicate with each
other using interprocess communication (IPC). On Android, one process cannot normally access the memory of another process. So to
talk, they need to decompose their objects into primitives that the operating system can understand, and marshall the objects across that
boundary for you. The code to do that marshalling is tedious to write, so Android handles it for you with AIDL.”</i><br>
https://developer.android.com/guide/components/aidl.html<br><br>

![alt tag](https://github.com/nervousnet/nervousnet-android/blob/master/Resources/Images/Others/ppt_screens/Slide3.jpg)
<br>
####Terminology
#####Mobile App - Native Mobile Application built for Android and iOS platforms. <br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Allows users to view and share various Sensor related Data<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Required to be installed for running external apps (Axons) built using nervousnet PlatformAPI’s.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Acts like a connectivity hub for external products like smartwatches, beacons and external sensors that want to share sensor data with the nervousnet platform.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Android version uses background Services to enable third party apps and extensions to connect and share data with the Nervousnet platform.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- iOS version uses WebViews and allows for external Axons to run inside a WebView container.<br><br>

#####Axons (Native) - Native Android apps, Smart devices, beacons that can connect to the nervousnet HUB mobile app.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Uses the nervousnet Platform API's to receive and share sensor data.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Works only in Android devices.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Uses the Android background services feature.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Possibility of using Bluetooth, Wi-Fi Direct to do connect to the nervousnet mobile app.<br><br>

#####Axons (Hybrid) - HTML, JavaScript and CSS applications that run inside WebView containers inside the nervousnet apps. <br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Currently supported on the iOS platform.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Android Platform support in the next phase.<br><br>

#####nervousnet CORE – Distributed and Decentralized set of Servers <br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Used to store and collect Data shared by Clients (Mobile & Web), IOT Hardware sensors and devices, partner platforms and more.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Individual Servers are called <b>nervousnet Nodes</b>.<br>
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;- Mobile Clients will have the option of selecting a server from a list.<br><br>


![alt tag](https://github.com/nervousnet/nervousnet-android/blob/master/Resources/Images/Others/ppt_screens/Slide5.jpg)
<br>
### GitHub structure
<small>
Root <br>
 |<br>
 &nbsp; -- <b>Deliverables</b> <i>( Contains installables, apk files for nervousnet mobile app.)</i><br>
 |<br>
 &nbsp; -- <b>Documents</b> <i>(Contains all documentation related to nervousnet and the Android Project)</i><br>
 |<br>
 &nbsp; -- <b>Mobile Clients</b> <i>( Contains nervousnet mobile app Android Project)</i><br>
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  |<br> 
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -- <b>Android</b><br>
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  |<br> 
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -- <b>Sample Extensionss</b> <i>( Contains native Axon App projects)</i><br>
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  |<br> 
  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -- <b>nervousnetHUB</b> <i>( Main nervousnet Android Mobile App Project)</i><br>
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  |<br> 
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -- <b>nervousnetLIB</b> <i>( nervousnet Library Project. Native Axon App projects should include into their project)</i><br>
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  |<br> 
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -- <b>nervousnetVM</b> <i>( nervousnet Virtual Machine, required by the main project)</i><br>
 &nbsp;  |<br> 
 &nbsp; -- <b>Resources</b> <i>( Contains all resource, raw images and final images that are used for the nervousnet Android Project)</i><br>
</small>

### Support or Contact
For more information or support check out our website http://www.nervousnet.info
