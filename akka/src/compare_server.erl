-module(compare_server).
-behaviour(gen_server).
-author("Piotr").

%% API
-export([start/0, stop/0, checkPrice/2, deleteMe/1]).
-export([init/1, handle_call/3, handle_cast/2]).

-record(queries, {productName, occurrences}).

%API
start() ->
  install_db(),
  gen_server:start_link({global, server}, ?MODULE, [], []).

stop() ->
  mnesia:stop(),
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
  Pid = spawn_link(compare, price_compare, [ProductName, ClientName]),
  {noreply, Children++[Pid]};

handle_cast(stop, Children) ->
  {stop, normal, Children}.

%server side functions
install_db() ->
  mnesia:create_schema([node()]),
  mnesia:start(),
  case mnesia:create_table(queries, [{attributes, record_info(fields, queries)}, {disc_copies, [node()]}]) of
    {atomic, ok} -> io:format("Table successfuly created~n");

    {aborted, {already_exists, queries}} -> io:format("Table queries already exists~n");

    _ -> io:format("Uknown error~n")
  end,
  mnesia:wait_for_tables([queries], 5000).