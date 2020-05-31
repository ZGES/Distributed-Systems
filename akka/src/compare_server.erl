-module(compare_server).
-behaviour(gen_server).
-author("Piotr").

%% API
-export([start/0, stop/0, checkPrice/2, deleteMe/1]).
-export([init/1, handle_call/3, handle_cast/2]).

%API
start() ->
  gen_server:start_link({local, server}, ?MODULE, [], []).

stop() ->
  gen_server:cast(server, stop).

checkPrice(Pid, ProductName) ->
  gen_server:cast(server, {compare, ProductName, Pid}).

deleteMe(Pid) ->
  handle_cast(server, {delete, Pid}).

%callback
init(_) ->
  {ok, []}.

handle_call(_,_,_) ->
  ok.

handle_cast({compare, ProductName, Pid}, Children) ->
  ChildPid = spawn(compare, price_compare, [ProductName, Pid]),
  {noreply, Children++[ChildPid]};

handle_cast({delete, Pid}, Children) ->
  exit(Pid, normal),
  {noreply, lists:delete(Children, Pid)};

handle_cast(stop, Children) ->
  {stop, normal, Children}.