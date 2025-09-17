//@author psilva
	public void selectComboBoxValue(String comboBoxId, String valueToSelect) {
		scrollWaitElementById(comboBoxId);
		logMsg("    (Base.selectComboBoxValue) ID:<%s>, value:<%s>", comboBoxId, valueToSelect);
		waitPage(wait);
		Set<String> seenOptions = new HashSet<>();
		WebElement comboBox = driver.findElement(By.id(comboBoxId)).findElement(By.tagName("input"));
		String currentValue = comboBox.getAttribute("value");
		boolean found = false;
		if (currentValue.equals("")) {
			if(valueToSelect.equals("")){
				found = true;
			} else {
				comboBox.click();
				waitPage(wait);
				comboBox.sendKeys(Keys.ARROW_DOWN);
				comboBox.sendKeys(Keys.ARROW_UP);
				comboBox.sendKeys(Keys.ENTER);
				currentValue = comboBox.getAttribute("value");

				if (currentValue.equals(valueToSelect)) {
					seenOptions.add(currentValue);
					found = true;
					waitPage(wait);
				} else {
					comboBox.click();
				}
			}
		} else {
			if (currentValue.equals(valueToSelect)) {
				comboBox.sendKeys(Keys.ENTER);
				seenOptions.add(currentValue);
				found = true;
				waitPage(wait);
			} else {
				comboBox.click();
				comboBox.sendKeys(Keys.ARROW_UP);
				waitPage(wait);
				currentValue = comboBox.getAttribute("value");
				if (currentValue.equals(valueToSelect)) {
					waitPage(wait);
					comboBox.sendKeys(Keys.ENTER);
					found = true;
					waitPage(wait);
				}
				seenOptions.add(currentValue);
			}
		}

		while (!found) {
			comboBox.sendKeys(Keys.ARROW_DOWN);
			waitPage(wait);
			currentValue = comboBox.getAttribute("value");
			if (seenOptions.contains(currentValue)) {
				comboBox.sendKeys(Keys.ENTER);
				fail(valueToSelect + " not found");
			}

			seenOptions.add(currentValue);

			if (currentValue.equals(valueToSelect)) {
				waitPage(wait);
				comboBox.sendKeys(Keys.ENTER);
				waitPage(wait);
				found = true;
			}
		}
		waitPage(wait);
	}