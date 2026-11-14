![FarmersBW](https://github.com/mehdiacho/Farmer.Registration.System/raw/v2.0.0/docs/screenshots/banner.png)

FarmersBW is a desktop app for registering Botswana's farmers. Farmers sign up and follow their review, and the Ministry approves registrations and prints reports on farmland in each district.

Version 2.0 rebuilds my 2022 university project. Java comes bundled in every download below, so you don't need to install it separately.

## Download

| Your computer | File | How to install |
|---|---|---|
| **Windows 10 or 11** | `FarmersBW-2.0.0-windows-x64.msi` | Double-click it and follow the steps. It installs for your user only, so no admin rights are needed. It adds a Start-menu entry and a desktop shortcut. |
| Windows, no install | `FarmersBW-2.0.0-windows-x64-portable.zip` | Unzip it anywhere, then run `FarmersBW\FarmersBW.exe`. |
| **macOS** (Apple silicon) | `FarmersBW-2.0.0-macos-arm64.dmg` | Open it and drag FarmersBW into Applications. |
| **Ubuntu / Debian** | `FarmersBW-2.0.0-linux-amd64.deb` | `sudo apt install ./FarmersBW-2.0.0-linux-amd64.deb` |
| Anything with Java 17+ | `FarmersBW-2.0.0-all.jar` | `java -jar FarmersBW-2.0.0-all.jar` |

**The installers are not code-signed**, so your computer will warn you the first time you open the app:

- **Windows**: SmartScreen shows "Windows protected your PC". Click **More info**, then **Run anyway**.
- **macOS**: right-click FarmersBW in Applications and choose **Open**. If macOS still refuses, go to **System Settings → Privacy & Security** and click **Open Anyway**.

`SHA256SUMS.txt` lists the checksum of every file.

## First run

On first run the app asks you to create the **admin account**. There is no default password. The same screen offers to add **40 fictional sample farmers**, so you can try the dashboard, the review queue and the reports straight away. Leave that box unticked for a real, empty register.

To try the farmer side, click **Register your farm** on the sign-in screen. Then sign in as admin to approve the new registration.

Your data stays on your computer, in `~/.farmersbw/farmersbw.db`. Uninstalling the app does not delete it.

## What's new in 2.0

- **A new look.** Inter type, a palette taken from the Botswana landscape, and a painted farm scene on the sign-in and sign-up screens.
- **Admin dashboard.** Counts at a glance, farmers by district, a status ring, and a review queue with one-click approve and decline.
- **Registrations.** Filter by status, search by name, email, ID or district, and approve, decline, edit or delete several farmers at once.
- **Reports.** Filter by district, sub-district, gender and status, then export a text report or a CSV.
- **Farmer page.** Each farmer sees where their registration stands and can keep their details up to date.
- **Under the hood.** An embedded SQLite database, so nothing else needs installing. Passwords are hashed with PBKDF2, and the code has a JUnit test suite.

The original 2022 version is kept in the repository under `history/` and at the `original-2023` tag.
