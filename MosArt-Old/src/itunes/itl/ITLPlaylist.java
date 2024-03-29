package itunes.itl;

/*
 *  titl - Tools for iTunes Libraries
 *  Copyright (C) 2008 Joseph Walton
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import itunes.itl.parsers.ITLHohmPodcast;

import java.util.ArrayList;
import java.util.List;

public class ITLPlaylist {
	byte[] ppid;
	String title;
	public byte[] smartInfo;
	public byte[] smartCriteria;

	private final List<Integer> items = new ArrayList<Integer>();
	private ITLHohmPodcast hohmPodcast;;

	public byte[] getPpid() {
		return ppid;
	}

	public String getTitle() {
		return title;
	}
	
	public List<Integer> getItems() {
		return items;
	}

	public ITLHohmPodcast getHohmPodcast() {
		return hohmPodcast;
	}
	
	public void setPPID(byte[] ppid) {
		this.ppid = ppid;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void addItem(int key) {
		items.add(Integer.valueOf(key));
	}
	
	public void setHohmPodcast(ITLHohmPodcast parse) {
		this.hohmPodcast = parse;
	}

}
