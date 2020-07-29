package uk.co.urbanfortress.aws.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import org.springframework.stereotype.Component;

import uk.co.urbanfortress.aws.ClipboardFactory;

@Component
public class DefaultClipboardFactory implements ClipboardFactory {

	@Override
	public Clipboard clipboard() {
		System.setProperty("java.awt.headless", "false");
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	

}
