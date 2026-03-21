# 📄 Master Functional Specifications: Rollease Acmeda Pulse 2 Hub
**Driver Branch:** The Surgical Observer  
**Integration Logic:** Parent/Child Architecture with Surgical Error Silencer

---

## 1. Project Context & Evolution

> ### The "Rollease Shade" Project Branch
> This driver represents a specialized development branch of the **Rollease Acmeda Pulse 2 Hub** integration, originally founded on the work of **Younes (@yannick00000)**. While the core ARC protocol remains the foundational layer, this branch has been optimized for high‑performance automation environments.

> ### The Discovery: The "Burst-and-Close" Logic
> Through systematic diagnostic testing using **PuTTY (Raw TCP/IP)**, we confirmed that the Pulse 2 Hub operates as a **"Burst Communicator."** The Hub is hardcoded to:
> 1.  **Open** a socket upon a command/request.
> 2.  **Execute** the command (RF transmission to the shade).
> 3.  **Dump** a high‑speed status burst (Position, Battery, Signal).
> 4.  **Terminate** the connection forcibly.
>
> We realized the `Stream is closed` error was never a network failure; it was the Hub's standard—albeit abrupt—way of finishing a transmission.

> ### The Surgical Resolution
> This branch reverts to the original, reliable **"Direct-Fire" communication timing** and adds a **Surgical Error Silencer**. 
> * **The Fix:** Instead of fighting the Hub to stay connected, the driver now intercepts the hang‑up, recognizes it as a successful data dump, and handles the disconnection gracefully.
> * **The Result:** We maintain the bulletproof reliability of the original driver while gaining clean logs and modern dashboard attributes.

---

## 2. Commands (Functional Methods)

> ### The Foundation Layer
> The following methods are the original, stable core of the **Younes (@yannick00000)** integration. We have retained these commands without structural modification to ensure 100% compatibility with the Hub's native ARC protocol handling.

* **`configure()`**: Standard Hubitat setup method. It sends `!000v?` to the hub, triggering a full status update and automatic discovery of child shades.
* **`initialize()`**: The "Cold Start." This method closes any existing telnet connection, flushes pending state, and initiates a fresh Telnet handshake with the Pulse 2 Hub.
* **`refresh()`**: (Required by capability) Not actively used for polling; kept for compatibility.
* **`sendTelnetCommand(String msg)`**: The internal logic method used by the driver to deliver commands to the Hub. Child devices call this method to issue shade commands.
* **`sendPulseCommand(String msg)`**, **`sendMsg(String msg)`**: Aliases for `sendTelnetCommand`, provided for compatibility with external automation.
* **`deleteAllChildDevices()`**: Utility command to remove all child shades created by the parent.