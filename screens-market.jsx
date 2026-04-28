// Market screens: list, detail, portfolio, create market, resolution.
// All sober, dense, trading-terminal vibe.

const MARKETS = [
  { cat: 'tech', icon: '🤖', q: 'Will OpenAI release GPT-6 before December 2026?', vol: '4.2M', yes: 67, end: 'Dec 31, 2026', traders: 1284 },
  { cat: 'tech', icon: '🚀', q: 'Will SpaceX land humans on Mars before 2030?', vol: '8.9M', yes: 23, end: 'Jan 1, 2030', traders: 3902 },
  { cat: 'sport', icon: '⚽', q: 'Will PSG win the Champions League 2026?', vol: '2.1M', yes: 18, end: 'Jun 1, 2026', traders: 945 },
  { cat: 'absurd', icon: '👽', q: 'Will the Pentagon publicly confirm UAP recovery before 2027?', vol: '780K', yes: 12, end: 'Jan 1, 2027', traders: 412 },
  { cat: 'tech', icon: '⚡', q: 'Will Anthropic raise at >$200B valuation in 2026?', vol: '1.8M', yes: 71, end: 'Dec 31, 2026', traders: 622 },
  { cat: 'sport', icon: '🏎️', q: 'Will Verstappen win F1 World Championship 2026?', vol: '3.4M', yes: 52, end: 'Nov 30, 2026', traders: 1547 },
  { cat: 'absurd', icon: '🎬', q: 'Will Christopher Nolan win Best Director at the Oscars 2027?', vol: '420K', yes: 38, end: 'Mar 1, 2027', traders: 234 },
  { cat: 'tech', icon: '📱', q: 'Will Apple ship a foldable iPhone in 2026?', vol: '1.1M', yes: 14, end: 'Dec 31, 2026', traders: 478 },
];

function MarketsList() {
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="markets"/>
        <div className="main">
          <Topbar title="Markets" balance="12,480.50"/>
          <div className="cat-row">
            <button className="cat-pill active">All</button>
            <button className="cat-pill"><Icon.Cpu style={{width: 11, height: 11}}/>Tech / AI</button>
            <button className="cat-pill"><Icon.Sport style={{width: 11, height: 11}}/>Sport</button>
            <button className="cat-pill"><Icon.Sparks style={{width: 11, height: 11}}/>Absurd</button>
            <div style={{flex: 1}}></div>
            <button className="cat-pill">Trending ↓</button>
            <button className="cat-pill">24h volume</button>
          </div>

          <div className="scroll">
            <div style={{padding: '16px 20px 0', display: 'flex', gap: 12, alignItems: 'baseline'}}>
              <div style={{fontSize: 12, color: 'var(--fg-3)'}}>
                <span className="mono tnum" style={{color: 'var(--fg-0)', fontWeight: 600}}>1,247</span> markets · <span className="mono tnum" style={{color: 'var(--fg-0)'}}>$48.2M</span> 24h volume
              </div>
              <div style={{flex: 1}}></div>
              <span className="chip live">LIVE</span>
            </div>

            <div className="market-grid">
              {MARKETS.map((m, i) => (
                <div key={i} className="market-card">
                  <div className="top">
                    <div className="icon">{m.icon}</div>
                    <div className="q">{m.q}</div>
                  </div>
                  <div className="prob">
                    <span className="mono tnum" style={{color: 'var(--yes)', fontWeight: 600, width: 30}}>{m.yes}%</span>
                    <div className="bar"><i style={{width: m.yes + '%'}}/></div>
                    <span className="mono tnum" style={{color: 'var(--fg-3)', width: 30, textAlign: 'right'}}>{100-m.yes}%</span>
                  </div>
                  <div className="actions">
                    <button className="btn btn-yes">Yes · <span className="mono tnum">{m.yes}¢</span></button>
                    <button className="btn btn-no">No · <span className="mono tnum">{100-m.yes}¢</span></button>
                  </div>
                  <div className="meta">
                    <span>Vol <span className="mono tnum" style={{color: 'var(--fg-1)'}}>${m.vol}</span></span>
                    <span>·</span>
                    <span className="mono tnum">{m.traders}</span><span> traders</span>
                    <span style={{flex: 1}}></span>
                    <span>Ends {m.end}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function MarketDetail({ outcome = 'pending' }) {
  // Sparkline-ish path
  const points = [10,20,15,30,25,40,35,50,45,55,60,67];
  const path = points.map((p,i) => `${i*60},${180-p*2}`).join(' L ');

  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="markets"/>
        <div className="main">
          <Topbar title="Markets / Detail" balance="12,480.50"/>
          <div className="scroll">
            <div style={{padding: '20px 20px 0'}}>
              <div style={{fontSize: 11, color: 'var(--fg-3)', marginBottom: 8}}>Markets · Tech / AI</div>
              <div style={{display: 'flex', alignItems: 'flex-start', gap: 14}}>
                <div style={{
                  width: 56, height: 56, borderRadius: 10,
                  background: 'var(--bg-2)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: 28
                }}>🤖</div>
                <div style={{flex: 1}}>
                  <h1 style={{margin: 0, fontSize: 20, fontWeight: 600, color: 'var(--fg-0)', lineHeight: 1.3}}>
                    Will OpenAI release GPT-6 before December 2026?
                  </h1>
                  <div style={{fontSize: 12, color: 'var(--fg-3)', marginTop: 6, display: 'flex', gap: 14}}>
                    <span>Volume <span className="mono tnum" style={{color: 'var(--fg-1)'}}>$4.21M</span></span>
                    <span>Liquidity <span className="mono tnum" style={{color: 'var(--fg-1)'}}>$890K</span></span>
                    <span>Ends <span style={{color: 'var(--fg-1)'}}>Dec 31, 2026</span></span>
                    <span className="chip live">LIVE</span>
                  </div>
                </div>
                <div style={{textAlign: 'right', whiteSpace: 'nowrap', flexShrink: 0}}>
                  <div style={{fontSize: 11, color: 'var(--fg-3)'}}>YES probability</div>
                  <div className="stat-big win" style={{whiteSpace: 'nowrap'}}>67<span style={{fontSize: 18, opacity: .6}}>%</span></div>
                  <div style={{fontSize: 11, color: 'var(--yes)'}} className="tnum">+4.2 · 24h</div>
                </div>
              </div>
            </div>

            <div className="detail">
              <div>
                <div className="chart">
                  <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8}}>
                    <div style={{fontSize: 12, color: 'var(--fg-2)'}}>Price history · YES</div>
                    <div style={{display: 'flex', gap: 4}}>
                      {['1H','24H','7D','1M','ALL'].map(p => (
                        <button key={p} className="chip" style={{cursor: 'pointer', background: p==='7D' ? 'var(--bg-3)' : 'var(--bg-2)', color: p==='7D' ? 'var(--fg-0)' : 'var(--fg-3)'}}>{p}</button>
                      ))}
                    </div>
                  </div>
                  <svg viewBox="0 0 660 170" style={{width: '100%', height: 160}} preserveAspectRatio="none">
                    <defs>
                      <linearGradient id="g1" x1="0" x2="0" y1="0" y2="1">
                        <stop offset="0%" stopColor="oklch(0.74 0.17 152)" stopOpacity="0.3"/>
                        <stop offset="100%" stopColor="oklch(0.74 0.17 152)" stopOpacity="0"/>
                      </linearGradient>
                    </defs>
                    {[0,40,80,120,160].map(y => (
                      <line key={y} x1="0" y1={y} x2="660" y2={y} stroke="oklch(0.28 0.012 260)" strokeDasharray="2 4"/>
                    ))}
                    <path d={`M 0,${180 - points[0]*2} L ${path} L 660,170 L 0,170 Z`} fill="url(#g1)"/>
                    <path d={`M 0,${180 - points[0]*2} L ${path}`} fill="none" stroke="oklch(0.74 0.17 152)" strokeWidth="2"/>
                    <circle cx="660" cy={180 - 67*2} r="4" fill="oklch(0.74 0.17 152)"/>
                  </svg>
                </div>

                <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginTop: 12}}>
                  <div className="card">
                    <div className="label">About this market</div>
                    <p style={{fontSize: 12, color: 'var(--fg-1)', margin: '4px 0 0', lineHeight: 1.5}}>
                      Resolves YES if OpenAI publicly releases a model branded as GPT-6 (general-availability or paid tier) before <span className="mono">2026-12-31 23:59 UTC</span>. Resolves based on official OpenAI announcement.
                    </p>
                  </div>
                  <div className="card">
                    <div className="label">Top holders</div>
                    {[
                      { name: '0xf3a…21c', side: 'YES', amt: '142K' },
                      { name: '0x88e…092', side: 'YES', amt: '98K' },
                      { name: 'whale.eth', side: 'NO', amt: '67K' },
                    ].map((h, i) => (
                      <div key={i} style={{display: 'flex', alignItems: 'center', gap: 8, padding: '4px 0', fontSize: 12}}>
                        <div style={{width: 18, height: 18, borderRadius: 9, background: 'var(--bg-3)'}}></div>
                        <span className="mono" style={{color: 'var(--fg-1)'}}>{h.name}</span>
                        <span className={`share-pill ${h.side.toLowerCase()}`}>{h.side}</span>
                        <span style={{flex: 1}}></span>
                        <span className="mono tnum" style={{color: 'var(--fg-2)'}}>${h.amt}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div>
                <div className="card" style={{padding: 14}}>
                  <div style={{display: 'flex', gap: 4, marginBottom: 12, background: 'var(--bg-2)', padding: 3, borderRadius: 6}}>
                    <button className="btn btn-yes" style={{flex: 1, height: 30}}>Yes <span className="mono tnum">67¢</span></button>
                    <button className="btn" style={{flex: 1, height: 30, background: 'transparent', border: 0, color: 'var(--fg-2)'}}>No <span className="mono tnum">33¢</span></button>
                  </div>
                  <div style={{display: 'flex', gap: 4, marginBottom: 14}}>
                    <button className="chip" style={{background: 'var(--bg-3)', color: 'var(--fg-0)'}}>Buy</button>
                    <button className="chip">Sell</button>
                    <span style={{flex: 1}}></span>
                    <button className="chip">Limit</button>
                  </div>

                  <label className="label">Amount ($PMC)</label>
                  <div style={{position: 'relative', marginBottom: 10}}>
                    <input className="input mono tnum" defaultValue="500.00"/>
                    <span style={{position: 'absolute', right: 12, top: 8, fontSize: 11, color: 'var(--fg-3)'}}>$PMC</span>
                  </div>
                  <div style={{display: 'flex', gap: 4, marginBottom: 14}}>
                    {['$25', '$100', '$500', 'MAX'].map(p => (
                      <button key={p} className="chip" style={{flex: 1, justifyContent: 'center', cursor: 'pointer'}}>{p}</button>
                    ))}
                  </div>

                  <div style={{display: 'grid', gap: 6, fontSize: 12, color: 'var(--fg-2)', padding: '10px 0', borderTop: '1px solid var(--line-soft)', borderBottom: '1px solid var(--line-soft)'}}>
                    <div style={{display: 'flex', justifyContent: 'space-between'}}>
                      <span>Avg price</span><span className="mono tnum">67.0¢</span>
                    </div>
                    <div style={{display: 'flex', justifyContent: 'space-between'}}>
                      <span>Shares</span><span className="mono tnum">746.27</span>
                    </div>
                    <div style={{display: 'flex', justifyContent: 'space-between'}}>
                      <span style={{color: 'var(--fg-1)'}}>Potential return</span>
                      <span className="mono tnum" style={{color: 'var(--yes)', fontWeight: 600}}>+$246.27 (49.2%)</span>
                    </div>
                  </div>

                  <button className="btn-gold" style={{width: '100%', marginTop: 14, background: 'var(--accent)', color: 'oklch(0.15 0.02 260)', boxShadow: 'none', textTransform: 'none', letterSpacing: 0}}>Buy YES · 746.27 shares</button>
                </div>

                <div className="orderbook" style={{marginTop: 12}}>
                  <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: 8, fontSize: 11, color: 'var(--fg-3)', fontFamily: 'var(--font-ui)', textTransform: 'uppercase', letterSpacing: '0.06em'}}>
                    <span>Order book</span><span>Spread 1.0¢</span>
                  </div>
                  <div className="row" style={{color: 'var(--fg-3)', fontSize: 10, paddingBottom: 4, borderBottom: '1px solid var(--line-soft)'}}>
                    <span>Price</span><span style={{textAlign: 'right'}}>Shares</span><span style={{textAlign: 'right'}}>Total</span>
                  </div>
                  {[
                    { p: '69.0', s: '420', t: '290', side: 'no' },
                    { p: '68.5', s: '1.2K', t: '822', side: 'no' },
                    { p: '68.0', s: '3.4K', t: '2.3K', side: 'no' },
                    { p: '67.5', s: '850', t: '574', side: 'no' },
                  ].map((r, i) => (
                    <div key={i} className="row" style={{color: 'var(--no)'}}>
                      <span>{r.p}¢</span><span style={{textAlign: 'right'}}>{r.s}</span><span style={{textAlign: 'right'}}>${r.t}</span>
                      <div style={{position: 'absolute', right: 0, top: 0, bottom: 0, width: '40%', background: 'var(--no-dim)', zIndex: -1}}/>
                    </div>
                  ))}
                  <div style={{padding: '6px 0', textAlign: 'center', fontWeight: 700, color: 'var(--fg-0)', fontSize: 13, borderTop: '1px solid var(--line-soft)', borderBottom: '1px solid var(--line-soft)'}}>
                    67.0¢
                  </div>
                  {[
                    { p: '66.5', s: '2.1K', t: '1.4K', side: 'yes' },
                    { p: '66.0', s: '4.5K', t: '2.9K', side: 'yes' },
                    { p: '65.5', s: '900', t: '589', side: 'yes' },
                    { p: '65.0', s: '1.8K', t: '1.1K', side: 'yes' },
                  ].map((r, i) => (
                    <div key={i} className="row" style={{color: 'var(--yes)'}}>
                      <span>{r.p}¢</span><span style={{textAlign: 'right'}}>{r.s}</span><span style={{textAlign: 'right'}}>${r.t}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function Portfolio() {
  const positions = [
    { q: 'Will OpenAI release GPT-6 before December 2026?', side: 'YES', shares: 746, avg: '62.0', cur: '67.0', val: '500', pnl: '+37.30', pnlpct: '+8.1' },
    { q: 'Will SpaceX land humans on Mars before 2030?', side: 'NO', shares: 1240, avg: '82.0', cur: '77.0', val: '954', pnl: '+62.00', pnlpct: '+6.5' },
    { q: 'Will PSG win the Champions League 2026?', side: 'YES', shares: 320, avg: '24.0', cur: '18.0', val: '57', pnl: '-19.20', pnlpct: '-25.0' },
    { q: 'Will Apple ship a foldable iPhone in 2026?', side: 'NO', shares: 880, avg: '88.0', cur: '86.0', val: '756', pnl: '-17.60', pnlpct: '-2.3' },
  ];
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="portfolio"/>
        <div className="main">
          <Topbar title="Portfolio" balance="12,480.50"/>
          <div className="scroll">
            <div style={{padding: '20px 20px 0', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12}}>
              {[
                { l: 'Total value', v: '$14,238.40', sub: '+$424 · 24h', good: true },
                { l: 'Cash ($PMC)', v: '12,480.50', sub: 'Available' },
                { l: 'Positions', v: '$1,757.90', sub: '4 open' },
                { l: 'Realized P/L', v: '+$2,140.20', sub: 'All time', good: true },
              ].map((s, i) => (
                <div key={i} className="card">
                  <div className="label">{s.l}</div>
                  <div className="mono tnum" style={{fontSize: 22, fontWeight: 700, color: 'var(--fg-0)'}}>{s.v}</div>
                  <div style={{fontSize: 11, color: s.good ? 'var(--yes)' : 'var(--fg-3)', marginTop: 2}} className="tnum">{s.sub}</div>
                </div>
              ))}
            </div>

            <div style={{padding: 20}}>
              <div style={{display: 'flex', alignItems: 'baseline', gap: 12, marginBottom: 10}}>
                <div style={{fontSize: 14, fontWeight: 600, color: 'var(--fg-0)'}}>Open positions</div>
                <div style={{fontSize: 11, color: 'var(--fg-3)'}}>4 markets</div>
              </div>
              <div className="card" style={{padding: 0, overflow: 'hidden'}}>
                <div style={{display: 'grid', gridTemplateColumns: '2fr 60px 80px 80px 80px 100px 90px', padding: '10px 14px', borderBottom: '1px solid var(--line-soft)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--fg-3)'}}>
                  <span>Market</span><span>Side</span><span style={{textAlign: 'right'}}>Shares</span><span style={{textAlign: 'right'}}>Avg</span><span style={{textAlign: 'right'}}>Current</span><span style={{textAlign: 'right'}}>Value</span><span style={{textAlign: 'right'}}>P/L</span>
                </div>
                {positions.map((p, i) => (
                  <div key={i} style={{display: 'grid', gridTemplateColumns: '2fr 60px 80px 80px 80px 100px 90px', padding: '12px 14px', borderBottom: i < 3 ? '1px solid var(--line-soft)' : 0, fontSize: 12, alignItems: 'center'}}>
                    <span style={{color: 'var(--fg-1)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', paddingRight: 12}}>{p.q}</span>
                    <span className={`share-pill ${p.side.toLowerCase()}`}>{p.side}</span>
                    <span className="mono tnum" style={{textAlign: 'right'}}>{p.shares}</span>
                    <span className="mono tnum" style={{textAlign: 'right', color: 'var(--fg-2)'}}>{p.avg}¢</span>
                    <span className="mono tnum" style={{textAlign: 'right', color: 'var(--fg-1)'}}>{p.cur}¢</span>
                    <span className="mono tnum" style={{textAlign: 'right', color: 'var(--fg-0)'}}>${p.val}</span>
                    <span className="mono tnum" style={{textAlign: 'right', color: p.pnl.startsWith('+') ? 'var(--yes)' : 'var(--no)', fontWeight: 600}}>{p.pnl}<br/><span style={{fontSize: 10, opacity: .8}}>{p.pnlpct}%</span></span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function CreateMarket() {
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="create"/>
        <div className="main">
          <Topbar title="Create market" balance="12,480.50"/>
          <div className="scroll">
            <div style={{padding: 24, maxWidth: 720, margin: '0 auto', width: '100%'}}>
              <div style={{fontSize: 11, color: 'var(--fg-3)', marginBottom: 4, textTransform: 'uppercase', letterSpacing: '0.08em'}}>Step 2 of 3</div>
              <h1 style={{fontSize: 22, color: 'var(--fg-0)', margin: 0, fontWeight: 600}}>Define your market</h1>
              <p style={{fontSize: 13, color: 'var(--fg-2)', marginTop: 6}}>A clear, binary, time-bounded question. Other traders will provide liquidity.</p>

              <div className="card" style={{marginTop: 20}}>
                <label className="label">Question</label>
                <input className="input" defaultValue="Will Anthropic release Claude 5 before September 2026?"/>
                <div style={{fontSize: 11, color: 'var(--fg-3)', marginTop: 4}}>Must resolve to a clear YES or NO based on a public source.</div>

                <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, marginTop: 14}}>
                  <div>
                    <label className="label">Category</label>
                    <select className="input"><option>Tech / AI</option><option>Sport</option><option>Absurd</option></select>
                  </div>
                  <div>
                    <label className="label">Resolution date</label>
                    <input className="input mono" defaultValue="2026-09-30"/>
                  </div>
                </div>

                <div style={{marginTop: 14}}>
                  <label className="label">Resolution source</label>
                  <input className="input mono" defaultValue="https://anthropic.com/news"/>
                </div>

                <div style={{marginTop: 14}}>
                  <label className="label">Initial liquidity ($PMC)</label>
                  <div style={{display: 'flex', gap: 10, alignItems: 'center'}}>
                    <input className="input mono tnum" defaultValue="500.00" style={{flex: 1}}/>
                    <div style={{fontSize: 11, color: 'var(--fg-3)'}}>min 100 · max 50,000</div>
                  </div>
                </div>

                <div style={{marginTop: 14, padding: 12, background: 'var(--bg-2)', borderRadius: 8, border: '1px solid var(--line-soft)'}}>
                  <div className="label" style={{marginBottom: 8}}>Initial probability</div>
                  <div style={{display: 'flex', alignItems: 'center', gap: 12}}>
                    <span className="mono tnum" style={{fontSize: 22, color: 'var(--yes)', fontWeight: 700, width: 60}}>50%</span>
                    <input type="range" min="1" max="99" defaultValue="50" style={{flex: 1, accentColor: 'oklch(0.72 0.16 230)'}}/>
                    <span className="mono tnum" style={{fontSize: 22, color: 'var(--no)', fontWeight: 700, width: 60, textAlign: 'right'}}>50%</span>
                  </div>
                </div>

                <div style={{display: 'flex', gap: 8, marginTop: 20, justifyContent: 'flex-end'}}>
                  <button className="btn">← Back</button>
                  <button className="btn-gold" style={{background: 'var(--accent)', color: 'oklch(0.15 0.02 260)', boxShadow: 'none', textTransform: 'none', letterSpacing: 0}}>Continue → Review</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function ResolutionWin() {
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="portfolio"/>
        <div className="main">
          <Topbar title="Position resolved" balance="12,480.50"/>
          <div style={{flex: 1, padding: '40px 60px', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', position: 'relative', overflow: 'hidden'}}>
            {/* confetti */}
            {Array.from({length: 24}).map((_, i) => {
              const colors = ['var(--gold)', 'var(--yes)', 'var(--neon)', 'var(--accent)'];
              return <div key={i} style={{
                position: 'absolute',
                top: 0,
                left: `${(i*4.2)%100}%`,
                width: 8, height: 8,
                background: colors[i % 4],
                animation: `confetti-fall ${2 + (i%5)*0.4}s ${(i*0.1)%2}s infinite linear`,
                opacity: 0.6,
              }}/>;
            })}

            <div style={{
              width: 64, height: 64, borderRadius: 32,
              background: 'oklch(0.74 0.17 152 / 0.15)',
              border: '1px solid var(--yes)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              marginBottom: 18,
            }}>
              <Icon.Check style={{width: 28, height: 28, color: 'var(--yes)'}}/>
            </div>
            <div style={{fontSize: 11, color: 'var(--yes)', textTransform: 'uppercase', letterSpacing: '0.12em', fontWeight: 600}}>Market resolved · YES</div>
            <h1 style={{fontFamily: 'var(--font-display)', fontSize: 38, fontWeight: 400, color: 'var(--fg-0)', margin: '8px 0 0', textAlign: 'center', maxWidth: 540, lineHeight: 1.2}}>
              You called it.
            </h1>
            <p style={{fontSize: 14, color: 'var(--fg-2)', textAlign: 'center', maxWidth: 480, marginTop: 6}}>
              "Will OpenAI release GPT-6 before December 2026?" resolved YES.
            </p>

            <div className="card" style={{marginTop: 22, width: 480, padding: 20}}>
              <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, paddingBottom: 14, borderBottom: '1px solid var(--line-soft)'}}>
                <div>
                  <div className="label">Position</div>
                  <div style={{fontSize: 13, color: 'var(--fg-1)'}}>746 shares · YES</div>
                  <div className="mono tnum" style={{fontSize: 11, color: 'var(--fg-3)', marginTop: 2}}>Avg 62.0¢ · Stake $462.52</div>
                </div>
                <div style={{textAlign: 'right'}}>
                  <div className="label">Payout</div>
                  <div className="mono tnum stat-big win">$746.00</div>
                  <div style={{fontSize: 11, color: 'var(--yes)'}} className="tnum">+$283.48 · +61.3%</div>
                </div>
              </div>
              <div style={{display: 'flex', gap: 10, marginTop: 16}}>
                <button className="btn" style={{flex: 1}}>Withdraw to wallet</button>
                <button className="btn-gold" style={{flex: 1, background: 'var(--accent)', color: 'oklch(0.15 0.02 260)', boxShadow: 'none', textTransform: 'none', letterSpacing: 0}}>Claim $746.00 →</button>
              </div>
            </div>

            <div style={{fontSize: 11, color: 'var(--fg-3)', marginTop: 18, textAlign: 'center'}}>
              Tap <span style={{color: 'var(--gold)', fontWeight: 600}}>Claim</span> to receive your payout in $PMC.
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function Onboarding() {
  return (
    <WindowChrome>
      <div style={{flex: 1, display: 'flex', overflow: 'hidden'}}>
        {/* Left side: brand panel */}
        <div style={{
          width: 360,
          background: 'linear-gradient(180deg, oklch(0.20 0.04 260), oklch(0.14 0.02 260))',
          padding: '40px 36px',
          borderRight: '1px solid var(--line-soft)',
          display: 'flex', flexDirection: 'column',
          position: 'relative',
          overflow: 'hidden',
        }}>
          <div style={{display: 'flex', alignItems: 'center', gap: 10}}>
            <div style={{width: 28, height: 28, borderRadius: 6, background: 'linear-gradient(135deg, var(--accent), var(--neon))'}}></div>
            <div style={{fontSize: 16, fontWeight: 700}}>Bet<span style={{color: 'var(--gold)'}}>Vault</span></div>
          </div>

          <h1 style={{
            fontFamily: 'var(--font-display)',
            fontWeight: 400,
            fontSize: 38,
            lineHeight: 1.1,
            margin: '50px 0 0',
            color: 'var(--fg-0)',
          }}>
            Trade what<br/>others won't.
          </h1>
          <p style={{fontSize: 14, color: 'var(--fg-2)', marginTop: 14, maxWidth: 280, lineHeight: 1.5}}>
            Predict the future on AI, sport, and the absurd. Or bet against the consensus.
          </p>

          <div style={{flex: 1}}></div>
          <div style={{fontSize: 11, color: 'var(--fg-3)', display: 'flex', gap: 10}}>
            <span>·</span><span>1,247 markets</span>
            <span>·</span><span>$48.2M today</span>
          </div>
        </div>

        {/* Right side: deposit */}
        <div style={{flex: 1, padding: '36px 60px', display: 'flex', flexDirection: 'column', overflow: 'auto'}}>
          <div style={{display: 'flex', gap: 4, marginBottom: 6}}>
            <div style={{width: 28, height: 3, background: 'var(--fg-0)', borderRadius: 2}}></div>
            <div style={{width: 28, height: 3, background: 'var(--fg-0)', borderRadius: 2}}></div>
            <div style={{width: 28, height: 3, background: 'var(--accent)', borderRadius: 2}}></div>
            <div style={{width: 28, height: 3, background: 'var(--bg-3)', borderRadius: 2}}></div>
          </div>
          <div style={{fontSize: 11, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Step 3 of 4 · Fund your account</div>
          <h1 style={{fontSize: 26, fontWeight: 600, margin: '4px 0 0', color: 'var(--fg-0)'}}>Deposit $PMC to start</h1>
          <p style={{fontSize: 13, color: 'var(--fg-2)', marginTop: 6}}>$PMC is the in-app credit. Used for both prediction markets and Casino plays. Top-up anytime.</p>

          <div style={{marginTop: 24, display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10}}>
            {[
              { v: '50', bonus: null },
              { v: '250', bonus: '+5%' },
              { v: '1,000', bonus: '+10%', featured: true },
            ].map((p, i) => (
              <button key={i} style={{
                padding: '18px 14px',
                background: p.featured ? 'oklch(0.84 0.16 86 / 0.08)' : 'var(--bg-1)',
                border: '1px solid ' + (p.featured ? 'var(--gold)' : 'var(--line-soft)'),
                borderRadius: 10,
                cursor: 'pointer',
                color: 'var(--fg-0)',
                textAlign: 'left',
                fontFamily: 'inherit',
                position: 'relative',
              }}>
                {p.bonus && <span style={{position: 'absolute', top: 8, right: 8, fontSize: 10, color: 'var(--gold)', fontWeight: 700}}>{p.bonus} bonus</span>}
                <div className="mono tnum" style={{fontSize: 24, fontWeight: 700}}>${p.v}</div>
                <div style={{fontSize: 11, color: 'var(--fg-3)', marginTop: 2}}>{p.v} $PMC{p.bonus ? ' + bonus' : ''}</div>
              </button>
            ))}
          </div>

          <label className="label" style={{marginTop: 20}}>Or enter custom amount</label>
          <div style={{position: 'relative'}}>
            <input className="input mono tnum" defaultValue="500.00" style={{height: 44, fontSize: 18}}/>
            <span style={{position: 'absolute', right: 14, top: 14, fontSize: 12, color: 'var(--fg-3)'}}>USD</span>
          </div>

          <div className="card" style={{marginTop: 18, padding: 12, background: 'var(--bg-2)'}}>
            <div className="label">Payment method</div>
            <div style={{display: 'flex', gap: 8, marginTop: 6}}>
              {['Card', 'USDC', 'Bank', 'Apple Pay'].map((m, i) => (
                <button key={i} className="chip" style={{cursor: 'pointer', flex: 1, justifyContent: 'center', height: 30, background: i === 0 ? 'var(--bg-3)' : 'var(--bg-1)', color: i === 0 ? 'var(--fg-0)' : 'var(--fg-2)'}}>{m}</button>
              ))}
            </div>
          </div>

          <div style={{flex: 1}}></div>
          <div style={{display: 'flex', gap: 10, marginTop: 24, alignItems: 'center'}}>
            <span style={{fontSize: 11, color: 'var(--fg-3)', flex: 1}}>By depositing you accept Terms · KYC verified ✓</span>
            <button className="btn">← Back</button>
            <button className="btn-gold" style={{background: 'var(--accent)', color: 'oklch(0.15 0.02 260)', boxShadow: 'none', textTransform: 'none', letterSpacing: 0}}>Deposit $500 →</button>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

Object.assign(window, { MarketsList, MarketDetail, Portfolio, CreateMarket, ResolutionWin, Onboarding, MARKETS });
