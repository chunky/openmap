// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies, a Verizon Company
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/util/wanderer/ChangeCase.java,v $
// $RCSfile: ChangeCase.java,v $
// $Revision: 1.1.1.1 $
// $Date: 2003/02/14 21:35:49 $
// $Author: dietrick $
// 
// **********************************************************************


package com.bbn.openmap.util.wanderer;

import java.io.*;
import java.util.*;

import com.bbn.openmap.util.ArgParser;
import com.bbn.openmap.util.Debug;

/** 
 * ChangeCase is a simple class that traverses a file system tree and
 * converts the contents to upper ot lower case letters, depending on
 * the options provided.
 * <pre>
 *  Usage: java com.bbn.openmap.util.wanderer.ChangeCase [-u|-l] (dir path 1)(dir path 2) ...  
 * </pre>
 */
public class ChangeCase extends Wanderer implements WandererCallback {

    boolean toUpper = false;

    public ChangeCase(boolean toUpperCase) {
	super();
	toUpper = toUpperCase;
	setCallback(this);
    }

    public void handleDirectory(File directory) {
	handleFile(directory);
    }

    public void handleFile(File file) {
	File newFile;
	String parent = file.getParent();
	
	if (parent != null) {
	    if (toUpper) {
		newFile = new File(parent, file.getName().toUpperCase());
	    } else {
		newFile = new File(parent, file.getName().toLowerCase());
	    }
	} else {
	    if (toUpper) {
		newFile = new File(file.getName().toUpperCase());
	    } else {
		newFile = new File(file.getName().toLowerCase());
	    }
	}
	
	if (file.renameTo(newFile)) {
	    System.out.println("Renamed " + (file.getParent() == null?".":file.getParent()) + File.separator + file.getName() + " to " + (newFile.getParent() == null?".":newFile.getParent()) + File.separator + newFile.getName());
	} else {
	    System.out.println("Renaming " + (file.getParent() == null?".":file.getParent()) + File.separator + file.getName() + " to " + (newFile.getParent() == null?".":newFile.getParent()) + File.separator + newFile.getName() + " FAILED");
	}
    }

    /**
     * Given a set of files or directories, parade through them to
     * change their case.
     * @param argv paths to files or directories, use -h to get a
     * usage statement.  
     */
    public static void main(String[] argv) {
	Debug.init();
	boolean toUpper = true;

	ArgParser ap = new ArgParser("ChangeCase");
	ap.add("upper", "Change file and directory names to UPPER CASE (default). <path> <path> ...", ArgParser.TO_END);
	ap.add("lower", "Change file and directory names to lower case. <path> <path> ...", ArgParser.TO_END);

	if (argv.length == 0) {
	    ap.bail("", true);
	}

	ap.parse(argv);

	String[] dirs;
	dirs = ap.getArgValues("lower");
	if (dirs != null) {
	    Debug.output("Converting to lower case names...");
	    toUpper = false;
	} else {
	    dirs = ap.getArgValues("upper");
	    // No arguments given, going to default.
	    if (dirs == null) {
		dirs = argv;
	    }
	    Debug.output("Converting to UPPER CASE names...");
	}

	ChangeCase cc = new ChangeCase(toUpper);

	// Assume that the arguments are paths to directories or
	// files.
	for (int i = 0; i < dirs.length; i++) {
	    cc.handleEntry(new File(dirs[i]));
	}
    }
}