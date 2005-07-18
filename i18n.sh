#  Copyright (c) 2003 David Kocher. All rights reserved.
#  http://cyberduck.ch/
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  Bug fixes, suggestions and comments should be sent to:
#  dkocher@cyberduck.ch

#!/bin/bash
        
usage() {
	echo ""
	echo "    Usage: i18n.sh --extractstrings"
	echo "    Usage: i18n.sh [-l <language>] --status"
	echo "    Usage: i18n.sh [-l <language>] --init"
	echo "    Usage: i18n.sh [-l <language>] [-n <nib>] [--force] --update"
	echo ""
	echo "<language> must be Japanese.lproj, French.lproj, Spanish.lproj, ..."
	echo "<nib> must be Preferences.nib, Main.nib, ..."
	echo ""
	echo "Call with no parameters to update all languages and all nib files"
	echo ""
}

init() {
	mkdir -p $language
	for nibfile in `ls en.lproj | grep .nib | grep -v ~.nib | grep -v .bak`; do
		echo "Copying $nibfile"
		nib=`basename $nibfile .nib`
		cp -R en.lproj/$nibfile $language/$nibfile
		rm -rf $language/$nibfile/CVS
		nibtool --localizable-strings $language/$nibfile > $language/$nib.strings
	done
	cp en.lproj/Localizable.strings $language/
	cp en.lproj/InfoPlist.strings $language/
	cp en.lproj/License.txt $language/
}

open() {
	nib=`basename $nibfile .nib`
	if [ "$language" = "all" ] ; then
	{
		for lproj in `ls . | grep lproj`; do
			if [ $lproj != "en.lproj" ]; then
				echo "*** Opening $lproj/$nib.strings"
				/usr/bin/open $lproj/$nib.strings
			fi;
		done;
	}
	else
	{
		echo "*** Opening $language/$nib.strings"
		/usr/bin/open $language/$nib.strings
	}
	fi;
}

extractstrings() {
    echo "*** Extracting strings from Obj-C source files (genstrings)..."
    genstrings -j -a -q -o en.lproj source/ch/cyberduck/ui/cocoa/*.java
    echo "*** Extracting strings from Java source files (genstrings)..."
    genstrings -j -a -q -o en.lproj source/ch/cyberduck/core/*.java
    genstrings    -a -q -o en.lproj source/ch/cyberduck/ui/cocoa/*.m
    genstrings -j -a -q -o en.lproj source/ch/cyberduck/core/ftp/*.java
    genstrings -j -a -q -o en.lproj source/ch/cyberduck/core/ftps/*.java
    genstrings -j -a -q -o en.lproj source/ch/cyberduck/core/sftp/*.java
}

status() {
	if [ "$language" = "all" ] ; then
	{
		for lproj in `ls . | grep lproj`; do
			language=$lproj;
			if [ $language != "en.lproj" ]; then
				echo "*** Status of $language Localization...";
				/usr/local/bin/polyglot -l `basename $language .lproj` .
			fi;
		done;
	}
	else
	{
		echo "*** Status of $language Localization...";
		/usr/local/bin/polyglot -l `basename $language .lproj` .
	}
	fi;
}

nib() {
    updateNibFromStrings;
    udpateStringsFromNib;
}

updateNibFromStrings() {
	rm -rf $language/$nibfile.bak 
    mv $language/$nibfile $language/$nibfile.bak

    if($force == true); then
	{
		# force update
		echo "*** Updating $nib... (force) in $language..."
		nibtool --write $language/$nibfile --dictionary $language/$nib.strings en.lproj/$nibfile
	}
    else
	{
		# incremental update
		echo "*** Updating $nib... (incremental) in $language..."
		nibtool --write $language/$nibfile \
				--incremental $language/$nibfile.bak \
				--dictionary $language/$nib.strings en.lproj/$nibfile
	}
    fi;
    cp -R $language/$nibfile.bak/CVS $language/$nibfile/CVS
	rm -rf $language/$nibfile.bak 
}

udpateStringsFromNib() {
    echo "*** Updating $nib.strings in $language..."
    nibtool --previous en.lproj/$nibfile \
            --incremental $language/$nibfile \
            --localizable-strings en.lproj/$nibfile > $language/$nib.strings
}

update() {
	if [ "$language" = "all" ] ; then
		{
			echo "*** Updating all localizations...";
			for lproj in `ls . | grep lproj`; do
				language=$lproj;
				if [ $language != "en.lproj" ]; then
				{
					echo "*** Updating $language Localization...";
					if [ "$nibfile" = "all" ] ; then
						echo "*** Updating all NIBs...";
						for nibfile in `ls $language | grep .nib | grep -v ~.nib | grep -v .bak`; do
							nib=`basename $nibfile .nib`
							nibtool --localizable-strings en.lproj/$nibfile > en.lproj/$nib.strings
							nib;
						done;
					fi;
					if [ "$nibfile" != "all" ] ; then
							nib=`basename $nibfile .nib`
							nibtool --localizable-strings en.lproj/$nibfile > en.lproj/$nib.strings
							nib;
					fi;
				}
				fi;
			done;
		}
	else
		{
			echo "*** Updating $language Localization...";
			if [ "$nibfile" = "all" ] ; then
				echo "*** Updating all NIBs...";
				for nibfile in `ls $language | grep .nib | grep -v ~.nib | grep -v .bak`; do
					nib=`basename $nibfile .nib`;
					nibtool --localizable-strings en.lproj/$nibfile > en.lproj/$nib.strings
					nib;
				done;
			fi;
			if [ "$nibfile" != "all" ] ; then
			{
				nib=`basename $nibfile .nib`;
				nibtool --localizable-strings en.lproj/$nibfile > en.lproj/$nib.strings
				nib;
			}
			fi;
		}
	fi;
}

language="all";
nibfile="all";
force=false;

while [ "$1" != "" ] # When there are arguments...
	do case "$1" in 
			-l | --language)
				shift;
				language=$1;
				echo "Using Language:$language";
				shift;
			;;
			-n | --nib) 
				shift;
				nibfile=$1;
				echo "Using Nib:$nibfile";
				shift;
			;;
			-f | --force) 
				force=true;
				shift;
			;;
			-g | --extractstrings)
				extractstrings;
				exit 0;
				echo "*** DONE. ***";
			;;
			-h | --help) 
				usage;
				exit 0;
				echo "*** DONE. ***";
			;; 
			-i | --init)
				echo "Init new localization...";
				init;
				echo "*** DONE. ***";
				exit 0;
			;; 
			-s | --status)
				echo "Status of localization...";
				status;
				echo "*** DONE. ***";
				exit 0;
			;; 
			-u | --update)
				echo "Updating localization...";
				update;
				echo "*** DONE. ***";
				exit 0;
			;; 
			-o | --open)
				echo "Opening localization .strings files...";
				open;
				echo "*** DONE. ***";
				exit 0;
			;; 
			*)  
				echo "Option [$1] not one of  [--extractstrings, --status, --update, --open, --init]"; # Error (!)
				exit 1
			;; # Abort Script Now
	esac;
done;
usage;
