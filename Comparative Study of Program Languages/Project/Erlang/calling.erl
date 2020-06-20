-module(calling).
-author("ndkcha").

%% API
-export([initCallingSequence/1]).

initCallingSequence(Contact) ->
  Name = element(1, Contact),
  Targets = element(2, Contact),
  CallIterator = fun(Elem) -> send(Name, Elem) end,
  lists:foreach(CallIterator, Targets),
  startReceiving(Name).

startReceiving(Name) ->
  receive
    { Sender, Stamp } ->
      MasterId = whereis(master),
      MasterId ! { Name, Sender, Stamp },
      AckId = whereis(Sender),
      AckId ! { Name, Stamp, 1 },
      startReceiving(Name);
    { Sender, Stamp, Rep } ->
      MasterId = whereis(master),
      MasterId ! { Name, Sender, Stamp, Rep },
      startReceiving(Name)
  after (1000) ->
    MasterId = whereis(master),
    MasterId ! { Name }
  end.

send(Name, Contact) ->
  timer:sleep(random:uniform(100)),
  TimeStamp = element(3, now()),
  TargetId = whereis(Contact),
  TargetId ! { Name, TimeStamp }.
