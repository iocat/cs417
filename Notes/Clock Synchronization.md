# Synchronization Problem
Covers interactions among distributed processes
| Domain | Synchronization Issue |
|:-|:-|
|Clocks| Identifying when something happened
| Mutual Exclusion | Only one entity can do an operation at a time
| Leader election| Who coordinates activity |
| Message consistency | Does everyone have the same view of the events? |
| Agreement | Can everyone agree on a proposed value? |

These problems are nontrivial and tricky in distributed systems.

# Clock Synchronization
To be able to identify "now."

Example: replication & and identifying the latest message.
Simple approach to this issue is with every message we always provide a timestamp

## Clocks 
2 types of clocks:
1. Physical clock: keep track time of the day.
1. Logical clock: Keep track of event ordering 

### 1. _Physical Clock_
Get two systems to agree on time

Why is this hard?
- Two clocks hardly agree
- Different oscilation of quartz oscillators

Tick at different rates, hence creates a *Clock Drift*

Jitter: short-term variation in frequency. One second might be longer than my another one second.

Different kinds of measure:
1. Astronomical time: time of day
2. Relative time: Count of seconds from epoch.

Dealing with Drift:
Not a good idea to set a clock back.
- Illusion of time moving backwards can confuse message ordering and software developments. 

Use Linear compensation: Slowly adjust the time frequency to slow down speed when the clock goes too fast.

#### Getting accurate time: 
Attach GPS receiver to each computer

### _Synchronize from a time server_
Simplest synchronization technique
- Send a network request to the server
- Set the time to the returned value.

**Cristian's algorithm**: Compensate for delays

The client sends the request at T0 in client's clock then the server sends the time upon receiving the request Tserver, then the client receives T1 in client's clock. 

The new time the client needs to set to is:
```
        Tnew = Tserver + (T1-T0)/2
```

**Berkeley's Algorithm**: Assumes no machine has an accurate time source

Take average time of all participating system.

**Network Time Protocol**

Enable clients accross Internet to be accurately synchronized to UTC despite delays

Provide reliable service
- Survive lengthy losses of connnectivity
- Redundant paths
- Redundant servers

Provide scalable service
- like DNS, they organize into a hierarchy

NTP Synchornization Modes: 
- Multicast Mode
- Procedure Call Mode (use Cristian Algorithm)
- Symmetric Mode

Quality:
- Precision
- Jitter

*Precision Time Protocol*

|  | NTP | PTP |
|- | -   |   - |
| Range | Nodes spread out on the Internet | Local Area networks |
| Accuracy | Several milliseconds on WAN | Sub-microsecond on LAN |


### 2. _Logical Clock_

Assign sequence numbers to messages
- All cooperating processes can agree on order of events
- vs. Physical Clocks: report time of day.

Assume no central time source
- Each system maintains its own logical clock 
- No total ordering of events

Assume multiple actors (Processes)
- Each process has a unique ID
- Each process has its own incrementing counter

#### Lamport's "happened-before" notation
Causality: a -> b event a happened before event b

Assign a clock value to each event
if a -> b then clock(a) \< clock(b)

If a and b occur on different processes that do not exchange messages, then neither a -> b or b -> are true. They are concurrent and causal.

If L(e) \< L(e'), we can't conclude e -> e'

By looking at Lamport timestamps, we **cannot** conclude which events are causally related. 

### Summary
- Causality: if a -> then event a can affect event b
- Concurrency: If neither a->b nor b->a is required then one event can't affect the other
- Partial Ordering: Causal events are ordered
- ....
