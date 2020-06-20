-module(exchange).
-author("ndkcha").

%% API
-export([start/0]).

start() ->
  {ok, Calls} = file:consult("calls.txt"),
  register(master, self()),
  io:fwrite("** Calls to be made **~n"),
  CallIterator = fun(Elem) -> spwanProcess(Elem) end,
  lists:foreach(CallIterator, Calls),
  io:fwrite("~n"),
  startReceiving().


spwanProcess(Contact) ->
  io:fwrite("~w~n", [Contact]),
  Pid = spawn(calling, initCallingSequence, [Contact]),
  register(element(1, Contact), Pid).

startReceiving() ->
  receive
    { Name, Sender, Stamp } ->
      io:fwrite("~w received intro message from ~w ( ~w ) ~n",[Name, Sender, Stamp]),
      startReceiving();
    { Name, Sender, Stamp, Rep } ->
      io:fwrite("~w received reply message from ~w ( ~w ) ~n",[Name, Sender, Stamp]),
      startReceiving();
    { Name } ->
      io:fwrite("Process ~w has received no calls for 1 second, ending... ~n", [Name]),
      startReceiving()
  after (1500) ->
    io:fwrite("Master has received no replies for 1.5 seconds, ending... ~n")
  end.