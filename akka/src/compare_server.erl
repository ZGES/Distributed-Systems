-module(compare_server).
-behaviour(gen_server).
-author("Piotr").

%% API
-export([start/0, stop/0, checkPrice/2, deleteMe/1]).
-export([init/1, handle_call/3, handle_cast/2]).

%API
start() ->
  gen_server:start_link({global, server}, ?MODULE, [], []).

stop() ->
  gen_server:cast(server, stop).

checkPrice(ProductName, ClientName) ->
  gen_server:cast({global, server}, {compare, ProductName, ClientName}).

deleteMe(Pid) ->
  gen_server:cast(server, {delete, Pid}).

%callback
init(_) ->
  {ok, []}.

handle_call(_,_,_) ->
  ok.

handle_cast({delete, Pid}, Children) ->
  exit(Pid, normal),
  {noreply, Children -- [Pid]};

handle_cast({compare, ProductName, ClientName}, Children) ->
  ChildPid = spawn_link(compare, price_compare, [ProductName, ClientName]),
  {noreply, Children++[ChildPid]};

handle_cast(stop, Children) ->
  {stop, normal, Children}.