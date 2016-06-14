/*
 * Copyright (C) 2010 Teleal GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bevis.demo.upnpdemoclient;

import java.io.Serializable;

import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.types.UDN;

import android.graphics.drawable.Drawable;
import android.view.Display;

/**
 * Wraps a <tt>Device</tt> for display with icon and label. Equality is
 * implemented with UDN comparison.
 * 
 */
public class DeviceItem implements Serializable {

	private UDN udn;
	private Device device;

	public DeviceItem(Device device) {
		this.udn = device.getIdentity().getUdn();
		this.device = device;
	}

	public DeviceItem(Device device, String... label) {
		this.udn = device.getIdentity().getUdn();
		this.device = device;
	}

	public UDN getUdn() {
		return udn;
	}

	public Device getDevice() {
		return device;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DeviceItem that = (DeviceItem) o;

		if (!udn.equals(that.udn))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return udn.hashCode();
	}

	@Override
	public String toString() {
		String display;

		if (device.getDetails().getFriendlyName() != null)
			display = device.getDetails().getFriendlyName();
		else
			display = device.getDisplayString();

		// Display a little star while the device is being loaded (see
		// performance optimization earlier)
		return device.isFullyHydrated() ? display : display + " *";
	}
}
