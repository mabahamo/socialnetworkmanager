/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import java.util.*;
import java.awt.Window;
import java.awt.Dimension;

/**
 * There is no mechanism in Swing supporting this, because the window
resizing is handled by the underlying windowing system. It's of course
possible to go to JNI and implement something, but here's a 100% java
way to detect this. Note that this is a very rough implementation, for
sure not production quality code. 
 * @author http://coding.derkeiler.com/Archive/Java/comp.lang.java.gui/2005-05/msg00287.html
 */public class WindowResizeMonitor implements Runnable {
private static final HashMap WINDOW_MAP = new HashMap();

private List listeners = new ArrayList();
private boolean run = false;
private Window window;

private WindowResizeMonitor(Window window) {
this.window = window;
}

public static void register(Window window, WindowResizeListener
listener) {
WindowResizeMonitor monitor = (WindowResizeMonitor)
WINDOW_MAP.get(window);

if (monitor == null) {
monitor = new WindowResizeMonitor(window);
WINDOW_MAP.put(window, monitor);
}
monitor.add(listener);
}

public static void unregister(Window window, WindowResizeListener
listener) {
WindowResizeMonitor monitor = (WindowResizeMonitor)
WINDOW_MAP.get(window);

if (monitor != null) {
monitor.remove(listener);
}
}

private synchronized void add(WindowResizeListener listener) {
listeners.add(listener);

if (!run) {
run = true;
new Thread(this).start();
}
}

private synchronized void remove(WindowResizeListener listener) {
listeners.remove(listener);

if (run && listeners.isEmpty()) {
run = false;
}
}

public void run() {
Dimension oldSize = window.getSize();

try {
while (run) {
Thread.sleep(500);

Dimension curSize = window.getSize();
if (!oldSize.equals(curSize)) {
fireWindowResizeEvent(new WindowResizeEvent(window,
oldSize, curSize));

oldSize = curSize;
}
}
} catch (InterruptedException e) {
}
}

private void fireWindowResizeEvent(WindowResizeEvent event) {
Iterator it = listeners.iterator();

while (it.hasNext()) {
WindowResizeListener l = (WindowResizeListener) it.next();
l.windowResized(event);
}
}
} 