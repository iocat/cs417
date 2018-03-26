# Mode of communication:
One to one
- unicast: a machine vs another
- anycast: 1 -> nearest 1 of several identical peers (used with BGP, introduced IPv6)

One to many
- multicast: 1 -> many, group communication

## Groups: Groups allow us to deal with a collection of processes as one abstraction (a logical abstraction)
Send message to one entity
- Deliver to entire group
Groups are dynamic
- Created and destroyed
- Proceeses can join or leave
Primitives
join\_group, leave\_group, send\_to\_group, query\_membership.

We want to build the group: Design Issues
1. Closed or open group
1. Closed: only group members can send messages
1. Peer vs Hierarchical: 
    1. Peer: each member communicates with group
    1. Hierarchical: go through dedicated coordinators
    1. Diffusion: send to other servers & clients
1. Managing membership & group creation/deletion
    1. Distributed and centralized
1. Leaving and joining: must be synchronous
1. Fault tolerance: 
    1. Reliable message delivery? What about missing members?

## Failure considerations
The same things bite us with group communication
1. Crash failure:
    - Process stops communicating
1. Omission Failure(typical due to network)
    - Send omission: a process fails to send messages
    - Receie omission: a process fails to receive message
1. Byzantine:
    - Some messages are faulty
    - .ie Bogus data or maliciously bogus or accidental bogus due to bad hard/software
1. Partition failure:
    - Network segremented error, dividing the network into multiple sub-groups
    - Common solution: Require a majority of the system to be around (a quora). If they are not, the system fails indefinitely

## Implementing Group Communication Mechanisms:

### Hardware Multicast and Broadcast
If we have hardware support for multicast
- Group members listen on network address

Ethernet supports both multicast & broadcast
Limited to local area networks

### Software: multiple unicasts
- Sender knows group members

# Reliability of multicasts:

## Atomicity: Message sent to a group arrives at all group members
    - If it fails to arrive at any member, no member will process it.

Problems: 

    Unreliable network:
        - Each message should be acknowledged
        - Acknowledgements can be lost
    Messsage sender might die



***Achieving atomicity***
General idea: 
- Ack from every recipients
- Only then allow the application to process the message
- If we give up on at least 1 recipient, then no recipient can process the message. 

***Achieving atomicity***
Retry through network failures and system downtime.
- Senders & receivers maintain a persistent log
- Each message has a unique ID so we can discard duplicates

***Reliable multicase***
All non-faulty group members will receive the message
- Assume sender & recipients will remain alive
- Network may have glitches

Acknowledgements:
- Send mess to each member
- Wait fro ack
- Retransmit to non-responding
- Subject to ***feedback implosion***

Negative Ack:
- Use a sequence # on each message
- Receive requests retransmission of a missed message
- More efficient but requires sender to buffer messages indefinitely.

## Ordering:

### Bad ordering:

### Sending versus Delivering
1. Deliver: to move the set of messages received from the sender to the application
1. Send: to receive the message and make a decision: to deliver or to drop

### Global time ordering:
1. All messages arrive in exact order sent
1. Assumes two events never happen at the exact same time. 

-> *Difficult(imossible) to achieve* because 
1. we cannot count on having absolutely precisely synchronized clock.
1. All time system has a certain precision (jitter).

So, The thing we do instead is: Total ordering

### Total ordering: Consistent ordering everywhere

All messages arrive at all group members in the same order they got sent

```
1. If a process sends m before m' then any other process that delivers m' will have delivered m.
2. If a process delivers m' before m'' then every other process will have delivered m' before m'' 
```

### Causal ordering

Partial ordering: messages sequenced by Lamport or Vector timestamps

```
    if multicast(G, m) -> multicast(G,m')
    then every process that delivers m' will have delivered m
```
### Sync ordering
### FIFO ordering
### Unordered multicast

### IP multicasting routing
- Deliver messages to a subset of nodes
- How to identify the recipients?
    - Enumerate them in the header?
        - What if we don't know
        - What if we have thousands of recipients?
    - Use a special adress to identify a group of receivers
        - A copy of the packet is delivered to all receivers associated with that group
        - Class D multicast IP address
        - Host group = set of machines listening to a particular multicast address.

IP started to shutting this down because of the amount of traffic it received. A lot of video conferencing system uses.

IP Multicast in use: IPTV.
IPTV has emerged as the biggest user of IP multicast.

# State Machine Replication

- We want high scalability and high availability
    - Achieve via redundancy.

- High availability: 