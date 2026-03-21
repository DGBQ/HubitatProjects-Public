# 📄 Change Notice: Shade Management (Parent Driver v3.2.0)

## **Overview**
This update adds manual shade management capabilities to the parent driver, allowing users to discover, add, and remove individual shades.  

It also enhances the existing `deleteAllChildDevices` command to provide better logging and state management.  

A new state variable, `shadeList`, is introduced to track known motor IDs.

---

## **1. New State Variables**

| Variable | Type | Purpose |
|----------|------|--------|
| `shadeList` | List of Strings | Stores motor IDs of all managed child devices. Automatically updated when children are created or removed. Used for validation and quick reference. |

**Initialization:**  
In `initialize()`, `shadeList` is populated using motor IDs from existing child devices via:
- `getChildDevices()`
- `motorAddress` data value

---

## **2. Enhanced Existing Commands**

### **`deleteAllChildDevices`**

**Purpose:**  
Removes all child shade devices.

---

### **Guardrails**
- Logs a warning before execution  
- Iterates through all child devices and deletes each one  
- Logs each deletion at **INFO** level  
- Clears `state.shadeList` after completion  

---

### **Logging**
```
[WARN] Deleting all child devices...
[INFO] Deleted child device: <name> (ID: <motorId>)
[INFO] All child devices deleted.
```

---

## **3. New Commands**

---

### **`discoverShades`**

**Purpose:**  
Forces the hub to return its device list and creates any missing child devices.

**Action:**  
Sends:
```
!000v?
```

The existing discovery logic will:
- Create new child devices for unknown motor IDs  
- Ignore already existing devices  

---

### **Guardrails**
- Logs a warning if the hub does not respond  
- Prevents duplicate child creation  

---

### **Logging**
```
[INFO] Discovering shades...
[INFO] Discovered and created new shade: <motorId>
[INFO] No new shades found.
[WARN] Hub did not respond to discovery command.
```

---

### **`addShade(String motorId)`**

**Purpose:**  
Manually creates a child device for a given motor ID.

**Parameter:**
- `motorId` → 3-character alphanumeric string (e.g., `I39`)

---

### **Guardrails**
- Validates:
  - Must be exactly 3 characters  
  - Must be uppercase letters and/or digits  
- Prevents duplicates  
- Updates `state.shadeList` on success  

---

### **Logging**
```
[INFO] Manually adding shade: <motorId>
[INFO] Shade <motorId> created.
[WARN] Shade <motorId> already exists.
[ERROR] Invalid motor ID: <motorId>. Must be 3 uppercase letters/digits.
```

---

### **`removeShade(String motorId)`**

**Purpose:**  
Deletes a child device for a specific motor ID.

**Parameter:**
- `motorId` → same format as above  

---

### **Guardrails**
- Verifies existence before deletion  
- Updates `state.shadeList` after removal  

---

### **Logging**
```
[INFO] Manually removing shade: <motorId>
[INFO] Shade <motorId> removed.
[WARN] Shade <motorId> does not exist.
```

---

## **4. Impact on Existing Logic**

### **Child Discovery (Parse)**
- When a new motor ID is detected:
  - A child device is created (if not already present)  
  - `state.shadeList` is updated  

---

### **Initialization**
- `state.shadeList` is rebuilt from existing child devices  
- Uses `motorAddress` data values  

---

### **Command Updates**
- `deleteAllChildDevices` now clears `state.shadeList`  

---

## **5. User Interface**

All commands appear as buttons on the device page:

| Command | Label |
|--------|------|
| `discoverShades` | Discover Shades |
| `addShade` | Add Shade (requires motor ID) |
| `removeShade` | Remove Shade (requires motor ID) |
| `deleteAllChildDevices` | Delete All Child Devices |

---

## **6. Guardrails & Failsafes Summary**

- Input validation for `addShade` and `removeShade`  
- Duplicate prevention for manual add  
- Existence check for manual removal  
- Structured logging at appropriate levels  
- `shadeList` always synchronized with actual devices  
- No automatic deletions (only explicit user actions)  

---

## **7. Code Impact**

- Changes are isolated to the **parent driver**  
- Child driver remains unchanged  

### **Additions**
- New commands added to metadata  
- Helper methods implemented  
- `parse()` updated to maintain `state.shadeList`  
- `initialize()` rebuilds `shadeList` from existing devices  