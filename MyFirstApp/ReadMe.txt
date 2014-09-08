Preface (Vorwort):
Widget va Gadget : la nhung tien ich nho tien ich voi nguoi dung.
Su khac nhau:

Doi voi Widget, ban co the sao chep chung den bat cu noi nao ma ma HTML duoc su dung.
Dieu do co nghia la ban hoan toan co the dua nhung Widget len blog hoac trang ca nhan tren cac mang xa hoi cua minh.
Nguoc lai, Gadget chi chay tren nhung trang web cu the hoac moi truong ma chung duoc thiet ke.
--------------------------------

The resources in Android app are stored in the "res" folder, which
includes layout, values, menu and a series of drawable subfolders.
 
Types of Android application:
	1. Forceground: the application is effectively suspended when it is not visible. Games are most common examples.
	2. Background:These applications are less common, 
	but good examples include call screening application, SMS auto-responder, and alarm clocks.
	3. Intermittent: Most well-designed application fall in this category.
	Union of visible activities and invisible background services.
	4. Widgets and Liver Wallpapers  
	
----------------------------------------
Android's application manifest:

The manifest file describes the component in the application and how they are bound togehter.
The manifest file is also used to specify the application's metadata, its hardware and platform requirements, external libraries 
and required permission.

---------------------------------------
uses-configuration in AndroidManifest.xml:
1) reqFiveWayNav: true or fasle, If it is set as "true", it can be navigated as left, right, top, button, and clicking on the current selection
2) reqKeyboardType : it is be one of following values: nokeys, qwerty, twelvekey or undefined
3) reqNavigation: nonav, dpad, trackball, wheel or undefined
4) reqTouchScreen: notouch, stylus, finger or undefined.

in addition, uses-feature specifies the version of OpenGL ES supported.
The example below shows how to enable OpenGL ES version 1.1:

<uses-feature android:glEsVersion=”0x00010001” />

---------------------------------------------------
uses-feature in AndroidManifest.xml

uses-feature node specifies which hardware features you application requires. e.g.
<uses-feature android:name=”android.hardware.nfc” />

Currently, optional hardware features include the following:
1) Audio — For applications that requires a low-latency audio pipeline. Note that at the
time of writing this book, no Android devices satisfi ed this requirement.
2) Bluetooth — Where a Bluetooth radio is required.
3) Camera — For applications that require a camera. You can also require (or set as
options) autofocus, fl ash, or a front-facing camera.
4) Location — If you require location-based services. You can also specify either net-
work or GPS support explicitly.
5) Microphone — For applications that require audio input.
6) NFC — Requires NFC (near-fi eld communications) support.
7) Sensors — Enables you to specify a requirement for any of the potentially available
hardware sensors.
8) Telephony — Specify that either telephony in general, or a specifi c telephony radio
(GSM or CDMA) is required.
‰ Touchscreen — To specify the type of touch-screen your application requires.
‰ USB — For applications that require either USB host or accessory mode support.
‰ Wi-Fi — Where Wi-Fi networking support is required.


----------------------------------------------------
supports-screens in AndroidManifest
supports-screens node specifies the screen size, which your applications are designed for and tested to.

e.g.    <supports-screens android:smallScreens=”false”
					android:normalScreens=”true”
					android:largeScreens=”true”
					android:xlargeScreens=”true”
					android:requiresSmallestWidthDp=”480”
					android:compatibleWidthLimitDp=”600”
					android:largestWidthLimitDp=”720”/>
					
-------------------------------
supports-gl-texture — Declares that the application is capable of providing texture
assets that are compressed using a particular GL texture compression format

<supports-gl-texture android:name=”GL_OES_compressed_ETC1_RGB8_texture” />

-----------------------------------------------
uses-permission — As part of the security model, uses-permission tags declare the user
permissions your application requires.

<uses-permission android:name=”android.permission.ACCESS_FINE_LOCATION”/>

--------------------------------------

permission — Your application components can also create permissions to restrict access
to shared application components.

<permission android:name=”com.paad.DETONATE_DEVICE”
		android:protectionLevel=”dangerous” 
		android:label=”Self Destruct”
		android:description=”@string/detonate_description”>
</permission>

protectionLevel can be one of: normal , dangerous , signature , signatureOrSystem
