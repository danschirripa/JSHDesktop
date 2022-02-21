/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package jshdesktop.com.pump.animation;

import jshdesktop.com.pump.util.Resettable;

/**
 * A combination of the <code>AnimationReader</code> and <code>Resettable</code>
 * interfaces.
 */
public interface ResettableAnimationReader extends AnimationReader, Resettable {

}