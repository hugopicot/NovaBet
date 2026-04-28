// OTO funnel + Casino screens (lobby, slot, loss/relance).
// This is the heart of the funnel: when the user wins on the market,
// the OTO modal appears at "Claim" time and re-routes them to Casino.

// ─── 3 OTO variations ────────────────────────────────────────────────
// Soft / Medium / Hard — same offer (x2/x5/x10 wheel), different aggression.

function OTOSoft() {
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="portfolio"/>
        <div className="main">
          <Topbar title="Claim payout" balance="12,480.50"/>
          {/* Background = win screen, dimmed */}
          <div style={{flex: 1, position: 'relative', background: 'var(--bg-0)'}}>
            <div style={{position: 'absolute', inset: 0, opacity: 0.25, padding: 40}}>
              <div className="card" style={{maxWidth: 480, margin: '40px auto'}}>
                <div className="stat-big win">$746.00</div>
              </div>
            </div>

            <div className="oto-backdrop" style={{background: 'oklch(0 0 0 / 0.55)'}}>
              <div style={{
                width: 440,
                background: 'var(--bg-1)',
                border: '1px solid var(--line-soft)',
                borderRadius: 12,
                padding: 26,
                boxShadow: '0 24px 60px oklch(0 0 0 / 0.5)',
              }}>
                <div style={{display: 'flex', alignItems: 'center', gap: 8, fontSize: 11, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 600, marginBottom: 8}}>
                  <Icon.Sparks style={{width: 12, height: 12}}/> Member offer
                </div>
                <div style={{fontFamily: 'var(--font-display)', fontSize: 28, color: 'var(--fg-0)', lineHeight: 1.2}}>
                  Multiply your win, on the house.
                </div>
                <p style={{fontSize: 13, color: 'var(--fg-2)', marginTop: 10, lineHeight: 1.5}}>
                  Convert your <span className="mono tnum" style={{color: 'var(--gold)', fontWeight: 600}}>$746.00</span> payout into Casino credits and spin the multiplier wheel — up to <span style={{color: 'var(--gold)', fontWeight: 600}}>10×</span>. No fee, no commitment.
                </p>

                <div style={{display: 'flex', gap: 10, padding: '14px 0', marginTop: 8, borderTop: '1px solid var(--line-soft)', borderBottom: '1px solid var(--line-soft)'}}>
                  {['2×','5×','10×'].map((m, i) => (
                    <div key={i} style={{flex: 1, textAlign: 'center'}}>
                      <div className="mono" style={{fontSize: 18, color: 'var(--gold)', fontWeight: 700}}>{m}</div>
                      <div style={{fontSize: 10, color: 'var(--fg-3)'}}>up to ${[1492, 3730, 7460][i].toLocaleString()}</div>
                    </div>
                  ))}
                </div>

                <div style={{display: 'flex', gap: 8, marginTop: 18}}>
                  <button className="btn" style={{flex: 1}}>No, withdraw $746</button>
                  <button className="btn-gold" style={{flex: 1.4}}>Spin the wheel →</button>
                </div>
                <div style={{fontSize: 10, color: 'var(--fg-3)', textAlign: 'center', marginTop: 12}}>
                  Casino credits cannot be withdrawn until wagered 1×.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function OTOMedium() {
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="portfolio"/>
        <div className="main">
          <Topbar title="Claim payout" balance="12,480.50"/>
          <div style={{flex: 1, position: 'relative', background: 'var(--bg-0)'}}>
            <div style={{position: 'absolute', inset: 0, opacity: 0.15, padding: 40}}>
              <div className="card" style={{maxWidth: 480, margin: '40px auto'}}><div className="stat-big win">$746.00</div></div>
            </div>

            <div className="oto-backdrop">
              <div className="oto-modal">
                <div style={{
                  position: 'absolute', top: -12, left: '50%', transform: 'translateX(-50%)',
                  background: 'var(--no)', color: '#fff', fontSize: 10, padding: '4px 12px', borderRadius: 999,
                  textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700,
                }}>
                  ⚡ Limited · expires in 4:38
                </div>

                <div style={{fontSize: 11, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.12em', fontWeight: 700}}>
                  ★ One-time offer ★
                </div>
                <h2 style={{fontFamily: 'var(--font-display)', fontSize: 36, fontWeight: 400, color: 'var(--fg-0)', margin: '12px 0 4px', lineHeight: 1.1}}>
                  Don't take <span style={{color: 'var(--gold)'}}>$746</span>.
                </h2>
                <h2 style={{fontFamily: 'var(--font-display)', fontSize: 36, fontWeight: 400, color: 'var(--gold)', margin: '0 0 14px', lineHeight: 1.1, textShadow: '0 0 24px oklch(0.84 0.16 86 / 0.4)'}}>
                  Take up to $7,460.
                </h2>

                <div style={{padding: '14px 0', borderTop: '1px solid oklch(0.84 0.16 86 / 0.2)', borderBottom: '1px solid oklch(0.84 0.16 86 / 0.2)', display: 'flex', gap: 8, justifyContent: 'center'}}>
                  {[
                    { m: '2×', v: '$1,492', odds: '60%' },
                    { m: '5×', v: '$3,730', odds: '30%' },
                    { m: '10×', v: '$7,460', odds: '10%' },
                  ].map((s, i) => (
                    <div key={i} style={{padding: '8px 14px', background: 'oklch(0.84 0.16 86 / 0.08)', borderRadius: 8, border: '1px solid oklch(0.84 0.16 86 / 0.3)'}}>
                      <div className="mono" style={{fontSize: 22, color: 'var(--gold)', fontWeight: 800}}>{s.m}</div>
                      <div className="mono tnum" style={{fontSize: 11, color: 'var(--fg-1)'}}>{s.v}</div>
                      <div style={{fontSize: 9, color: 'var(--fg-3)', marginTop: 2}}>{s.odds}</div>
                    </div>
                  ))}
                </div>

                <div style={{display: 'flex', flexDirection: 'column', gap: 8, marginTop: 18}}>
                  <button className="btn-gold pulse" style={{height: 48, fontSize: 14}}>SPIN THE WHEEL · $746 → up to $7,460</button>
                  <button style={{
                    background: 'transparent', border: 0, color: 'var(--fg-3)', fontSize: 12, fontFamily: 'inherit', cursor: 'pointer', padding: 6,
                    textDecoration: 'underline',
                  }}>No thanks, I'll take the smaller amount</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

function OTOHard() {
  return (
    <WindowChrome>
      <div className="shell">
        <Sidebar active="portfolio"/>
        <div className="main">
          <Topbar title="Claim payout" balance="12,480.50"/>
          <div style={{flex: 1, position: 'relative', background: 'var(--bg-0)'}}>
            {/* confetti */}
            {Array.from({length: 30}).map((_, i) => {
              const colors = ['var(--gold)', 'var(--neon)', 'var(--accent)', 'var(--yes)'];
              return <div key={i} style={{
                position: 'absolute', top: 0, left: `${(i*3.5)%100}%`,
                width: 8, height: 14,
                background: colors[i % 4],
                animation: `confetti-fall ${2.4 + (i%5)*0.4}s ${(i*0.08)%2}s infinite linear`,
                opacity: 0.7, zIndex: 1,
              }}/>;
            })}

            <div className="oto-backdrop" style={{background: 'radial-gradient(circle at center, oklch(0.20 0.10 320 / 0.85), oklch(0 0 0 / 0.92))'}}>
              <div className="oto-modal" style={{width: 540, padding: 32}}>
                {/* Scarcity bar */}
                <div style={{
                  position: 'absolute', top: 0, left: 0, right: 0,
                  background: 'linear-gradient(90deg, var(--no), var(--gold))',
                  color: '#000', fontSize: 10, padding: '6px 12px',
                  textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 800,
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                  borderRadius: '14px 14px 0 0', whiteSpace: 'nowrap',
                }}>
                  <span>⚠ ONE-TIME OFFER · NEVER REOPENS</span>
                  <span className="mono tnum">04:37</span>
                </div>

                <div style={{marginTop: 20, fontSize: 12, color: 'var(--neon)', textTransform: 'uppercase', letterSpacing: '0.14em', fontWeight: 800}}>
                  🔥 Winner exclusive · OTO 1/1
                </div>
                <div style={{
                  fontFamily: 'var(--font-display)', fontSize: 56, lineHeight: 0.95,
                  margin: '14px 0 6px', letterSpacing: '-0.02em',
                  background: 'linear-gradient(135deg, var(--gold), oklch(0.94 0.10 60))',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  textShadow: '0 0 60px oklch(0.84 0.16 86 / 0.5)',
                }}>
                  TURN $746<br/>INTO $7,460.
                </div>
                <div style={{fontSize: 14, color: 'var(--fg-1)', marginTop: 6}}>
                  Spin <b style={{color: 'var(--gold)'}}>once</b>. Lock <b style={{color: 'var(--gold)'}}>10×</b>. Walk out a legend.
                </div>

                <div style={{
                  margin: '20px 0', padding: 16,
                  background: 'oklch(0 0 0 / 0.4)', borderRadius: 10,
                  border: '1px dashed oklch(0.84 0.16 86 / 0.5)',
                }}>
                  <div style={{display: 'flex', alignItems: 'center', gap: 12}}>
                    <div className="mono tnum" style={{fontSize: 20, color: 'var(--fg-3)', textDecoration: 'line-through'}}>$746</div>
                    <Icon.ArrowRight style={{width: 18, height: 18, color: 'var(--gold)'}}/>
                    <div className="mono tnum" style={{fontSize: 32, color: 'var(--gold)', fontWeight: 800}}>up to $7,460</div>
                    <div style={{flex: 1}}></div>
                    <div className="chip" style={{background: 'var(--no)', color: '#fff', fontWeight: 700}}>+900%</div>
                  </div>
                </div>

                <div style={{fontSize: 11, color: 'var(--fg-2)', marginBottom: 6}}>
                  ✓ 1 free spin · ✓ No deposit · ✓ Credits applied instantly · ✓ Withdrawable after 1× wager
                </div>

                <button className="btn-gold pulse" style={{width: '100%', height: 56, fontSize: 16, marginTop: 14, letterSpacing: '0.04em'}}>
                  YES — SPIN MY $746 NOW →
                </button>
                <button style={{
                  background: 'transparent', border: 0, color: 'var(--fg-4)', fontSize: 11,
                  fontFamily: 'inherit', cursor: 'pointer', padding: 10, marginTop: 4, width: '100%',
                  textDecoration: 'underline',
                }}>
                  No, I'll pass on free $6,714 (you cannot reopen this)
                </button>

                <div style={{display: 'flex', gap: 14, justifyContent: 'center', marginTop: 14, fontSize: 10, color: 'var(--fg-3)'}}>
                  <span>★★★★★ 4.9</span>
                  <span>· 12,480 winners spun this week</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

// ─── Wheel of multipliers ────────────────────────────────────────────
function WheelScreen({ spinning = false, landed = '5×' }) {
  const slices = [
    { m: '2×', color: 'oklch(0.55 0.18 230)' },
    { m: '10×', color: 'oklch(0.68 0.28 340)' },
    { m: '2×', color: 'oklch(0.55 0.18 230)' },
    { m: '5×', color: 'oklch(0.84 0.16 86)' },
    { m: '2×', color: 'oklch(0.55 0.18 230)' },
    { m: '5×', color: 'oklch(0.84 0.16 86)' },
    { m: '2×', color: 'oklch(0.55 0.18 230)' },
    { m: '5×', color: 'oklch(0.84 0.16 86)' },
  ];
  const segDeg = 360 / slices.length;
  // Build conic-gradient
  let acc = 0;
  const stops = slices.map(s => { const start = acc; acc += segDeg; return `${s.color} ${start}deg ${acc}deg`; }).join(',');

  return (
    <WindowChrome casino>
      <div className="casino-shell">
        <div className="topbar" style={{background: 'oklch(0.13 0.04 310)', borderBottomColor: 'oklch(0.84 0.16 86 / 0.2)'}}>
          <div style={{display: 'flex', alignItems: 'center', gap: 10}}>
            <div style={{width: 22, height: 22, borderRadius: 5, background: 'linear-gradient(135deg, var(--gold), var(--neon))'}}></div>
            <span style={{fontFamily: 'var(--font-display)', fontSize: 18, color: 'var(--gold)'}}>Bet<span style={{color: 'var(--fg-0)'}}>Vault</span> Casino</span>
          </div>
          <div className="chip" style={{background: 'oklch(0.84 0.16 86 / 0.15)', color: 'var(--gold)', height: 24}}>
            🎁 OTO ACTIVE · $746 in play
          </div>
          <div style={{flex: 1}}></div>
          <div className="balance-pill" style={{background: 'oklch(0 0 0 / 0.4)', borderColor: 'oklch(0.84 0.16 86 / 0.3)'}}>
            <div className="coin">P</div>
            <span className="mono tnum">12,480.50</span>
          </div>
        </div>

        <div style={{flex: 1, padding: 36, display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative', zIndex: 2}}>
          <div className="casino-h1">Spin to multiply</div>
          <div style={{fontSize: 13, color: 'var(--fg-2)', marginTop: 4}}>One free spin on your $746 winning · select multiplier</div>

          <div className="wheel-wrap">
            <div className="wheel-pointer"></div>
            <div className="wheel" style={{
              background: `conic-gradient(${stops})`,
              transform: spinning ? 'rotate(1080deg)' : `rotate(${-segDeg*3 - segDeg/2}deg)`,
            }}>
              {slices.map((s, i) => {
                const angle = i * segDeg + segDeg/2;
                return (
                  <div key={i} style={{
                    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
                    transform: `rotate(${angle}deg)`,
                  }}>
                    <div style={{
                      position: 'absolute', top: 18, left: '50%', transform: 'translateX(-50%)',
                      fontFamily: 'var(--font-mono)', fontWeight: 800, fontSize: 18,
                      color: 'oklch(0.18 0.04 60)',
                      textShadow: '0 1px 2px oklch(1 0 0 / 0.3)',
                    }}>{s.m}</div>
                  </div>
                );
              })}
            </div>
            <div className="wheel-hub">SPIN</div>
          </div>

          <div style={{display: 'flex', gap: 24, marginTop: 24, alignItems: 'center'}}>
            <div style={{textAlign: 'center'}}>
              <div style={{fontSize: 11, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Stake</div>
              <div className="mono tnum" style={{fontSize: 24, color: 'var(--fg-0)', fontWeight: 700}}>$746</div>
            </div>
            <Icon.ArrowRight style={{width: 24, height: 24, color: 'var(--gold)'}}/>
            <div style={{textAlign: 'center'}}>
              <div style={{fontSize: 11, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Landed on</div>
              <div className="mono tnum stat-big gold" style={{fontSize: 36}}>{landed}</div>
            </div>
            <Icon.ArrowRight style={{width: 24, height: 24, color: 'var(--gold)'}}/>
            <div style={{textAlign: 'center'}}>
              <div style={{fontSize: 11, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Casino credits</div>
              <div className="mono tnum" style={{fontSize: 28, color: 'var(--gold)', fontWeight: 800, textShadow: '0 0 16px oklch(0.84 0.16 86 / 0.5)'}}>$3,730</div>
            </div>
          </div>

          <button className="btn-gold pulse" style={{marginTop: 26, height: 50, fontSize: 14, padding: '0 36px'}}>
            Play with $3,730 in casino →
          </button>
          <div style={{fontSize: 10, color: 'var(--fg-3)', marginTop: 10}}>
            Wager 1× to convert credits to withdrawable $PMC
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

// ─── Casino lobby ────────────────────────────────────────────────────
function CasinoLobby() {
  const games = [
    { name: 'Diamond Slots', emoji: '💎', tag: 'Featured', rtp: '96.4', players: 482, featured: true },
    { name: 'Crash Rocket', emoji: '🚀', tag: 'Hot', rtp: '97.0', players: 1240 },
    { name: 'Roulette Royale', emoji: '🎰', tag: 'Classic', rtp: '97.3', players: 320 },
    { name: 'Blackjack 21', emoji: '🃏', tag: 'Skill', rtp: '99.5', players: 188 },
    { name: 'Mines', emoji: '💣', tag: 'New', rtp: '99.0', players: 642 },
    { name: 'Plinko', emoji: '🔻', tag: 'Casual', rtp: '98.2', players: 904 },
  ];
  return (
    <WindowChrome casino>
      <div className="casino-shell">
        <div className="topbar" style={{background: 'oklch(0.13 0.04 310)', borderBottomColor: 'oklch(0.84 0.16 86 / 0.2)', position: 'relative', zIndex: 2}}>
          <div style={{display: 'flex', alignItems: 'center', gap: 10}}>
            <div style={{width: 22, height: 22, borderRadius: 5, background: 'linear-gradient(135deg, var(--gold), var(--neon))'}}></div>
            <span style={{fontFamily: 'var(--font-display)', fontSize: 18, color: 'var(--gold)'}}>Bet<span style={{color: 'var(--fg-0)'}}>Vault</span> Casino</span>
          </div>
          <button className="chip" style={{background: 'oklch(0 0 0 / 0.3)', color: 'var(--fg-2)', cursor: 'pointer'}}>← Back to markets</button>
          <div style={{flex: 1}}></div>
          <div className="balance-pill" style={{background: 'oklch(0.84 0.16 86 / 0.12)', borderColor: 'var(--gold)'}}>
            <div className="coin">P</div>
            <span className="mono tnum" style={{color: 'var(--gold)'}}>3,730.00</span>
            <span style={{fontSize: 10, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700}}>Casino credit</span>
          </div>
        </div>

        <div style={{padding: '24px 24px 0', position: 'relative', zIndex: 2}}>
          {/* Hero ribbon */}
          <div style={{
            background: 'linear-gradient(135deg, oklch(0.20 0.10 340), oklch(0.18 0.08 30))',
            border: '1px solid oklch(0.84 0.16 86 / 0.3)',
            borderRadius: 14, padding: 20,
            display: 'flex', alignItems: 'center', gap: 20,
            position: 'relative', overflow: 'hidden',
          }}>
            <div style={{flex: 1}}>
              <div style={{fontSize: 11, color: 'var(--neon)', textTransform: 'uppercase', letterSpacing: '0.12em', fontWeight: 700}}>★ Boost active</div>
              <div className="casino-h1" style={{fontSize: 26, marginTop: 4}}>
                Your $3,730 prize is hot.
              </div>
              <div style={{fontSize: 12, color: 'var(--fg-2)', marginTop: 4}}>
                Wager 1× to unlock withdrawal · $0 / $3,730 wagered
              </div>
              <div style={{height: 6, background: 'oklch(0 0 0 / 0.4)', borderRadius: 3, marginTop: 10, overflow: 'hidden'}}>
                <div style={{width: '4%', height: '100%', background: 'linear-gradient(90deg, var(--gold), var(--neon))'}}></div>
              </div>
            </div>
            <div style={{textAlign: 'right'}}>
              <div style={{fontSize: 10, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Expires in</div>
              <div className="countdown" style={{color: 'var(--gold)'}}>23:42:18</div>
            </div>
          </div>
        </div>

        <div className="cat-row" style={{borderBottom: 'none', position: 'relative', zIndex: 2}}>
          <button className="cat-pill active" style={{background: 'var(--gold)', color: 'oklch(0.18 0.04 60)', borderColor: 'var(--gold)'}}>All</button>
          <button className="cat-pill" style={{background: 'oklch(0 0 0 / 0.3)', borderColor: 'oklch(0.84 0.16 86 / 0.2)', color: 'var(--fg-1)'}}>Slots</button>
          <button className="cat-pill" style={{background: 'oklch(0 0 0 / 0.3)', borderColor: 'oklch(0.84 0.16 86 / 0.2)', color: 'var(--fg-1)'}}>Crash</button>
          <button className="cat-pill" style={{background: 'oklch(0 0 0 / 0.3)', borderColor: 'oklch(0.84 0.16 86 / 0.2)', color: 'var(--fg-1)'}}>Table games</button>
          <button className="cat-pill" style={{background: 'oklch(0 0 0 / 0.3)', borderColor: 'oklch(0.84 0.16 86 / 0.2)', color: 'var(--fg-1)'}}>New</button>
        </div>

        <div className="scroll" style={{position: 'relative', zIndex: 2, padding: '8px 24px 24px'}}>
          <div style={{display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14}}>
            {games.map((g, i) => (
              <div key={i} className={`casino-card ${g.featured ? 'featured' : ''}`} style={{padding: 0, height: 200, display: 'flex', flexDirection: 'column', cursor: 'pointer'}}>
                <div style={{
                  flex: 1,
                  background: g.featured
                    ? 'radial-gradient(circle at 30% 20%, oklch(0.84 0.16 86 / 0.4), transparent 60%), linear-gradient(135deg, oklch(0.30 0.12 340), oklch(0.20 0.08 30))'
                    : 'linear-gradient(135deg, oklch(0.22 0.08 320), oklch(0.16 0.06 310))',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: 64, position: 'relative',
                }}>
                  {g.emoji}
                  {g.tag && <span style={{
                    position: 'absolute', top: 10, left: 10,
                    fontSize: 10, padding: '3px 8px', borderRadius: 4,
                    background: g.featured ? 'var(--gold)' : 'oklch(0 0 0 / 0.5)',
                    color: g.featured ? 'oklch(0.18 0.04 60)' : 'var(--gold)',
                    fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em',
                    border: g.featured ? 'none' : '1px solid oklch(0.84 0.16 86 / 0.4)',
                  }}>{g.tag}</span>}
                </div>
                <div style={{padding: '10px 12px', borderTop: '1px solid oklch(0.84 0.16 86 / 0.15)'}}>
                  <div style={{fontSize: 13, fontWeight: 600, color: 'var(--fg-0)'}}>{g.name}</div>
                  <div style={{display: 'flex', gap: 12, fontSize: 10, color: 'var(--fg-3)', marginTop: 3}}>
                    <span>RTP <span className="mono tnum" style={{color: 'var(--gold)'}}>{g.rtp}%</span></span>
                    <span>·</span>
                    <span><span className="mono tnum">{g.players}</span> playing</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

// ─── Slot machine playable ─────────────────────────────────────────
function SlotMachine({ spin = 0, balance = '3,580' }) {
  const symbols = ['💎','🍒','7️⃣','⭐','🔔','🍋'];
  // Pre-baked reel positions per spin state for visual variety.
  const states = [
    [0, 1, 2],   // initial
    [0, 0, 0],   // jackpot 💎💎💎
  ];
  const positions = states[spin % states.length];

  return (
    <WindowChrome casino>
      <div className="casino-shell">
        <div className="topbar" style={{background: 'oklch(0.13 0.04 310)', borderBottomColor: 'oklch(0.84 0.16 86 / 0.2)', position: 'relative', zIndex: 2}}>
          <div style={{display: 'flex', alignItems: 'center', gap: 10, whiteSpace: 'nowrap', flexShrink: 0}}>
            <div style={{width: 22, height: 22, borderRadius: 5, background: 'linear-gradient(135deg, var(--gold), var(--neon))'}}></div>
            <span style={{fontFamily: 'var(--font-display)', fontSize: 18, color: 'var(--gold)', whiteSpace: 'nowrap'}}>Diamond Slots</span>
          </div>
          <button className="chip" style={{background: 'oklch(0 0 0 / 0.3)', color: 'var(--fg-2)', cursor: 'pointer', flexShrink: 0}}>← Lobby</button>
          <div style={{flex: 1}}></div>
          <div className="balance-pill" style={{background: 'oklch(0.84 0.16 86 / 0.12)', borderColor: 'var(--gold)'}}>
            <div className="coin">P</div>
            <span className="mono tnum" style={{color: 'var(--gold)'}}>{balance}.00</span>
          </div>
        </div>

        <div style={{flex: 1, display: 'grid', gridTemplateColumns: '1fr 320px', gap: 20, padding: 24, position: 'relative', zIndex: 2}}>
          <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 18}}>
            {spin > 0 && (
              <div style={{
                fontFamily: 'var(--font-display)', fontSize: 28, color: 'var(--gold)',
                textShadow: '0 0 30px oklch(0.84 0.16 86 / 0.6)',
                animation: 'pulse-gold 1s infinite',
                marginBottom: 18,
                whiteSpace: 'nowrap',
              }}>
                ✨ JACKPOT — +$1,500
              </div>
            )}
            <div className="slot-frame" style={{width: 460}}>
              <div className="slot-window">
                {positions.map((pos, ri) => (
                  <div key={ri} className="slot-reel">
                    <div className="reel-track" style={{transform: `translateY(-${pos * 128}px)`}}>
                      {symbols.map((s, si) => <div key={si}>{s}</div>)}
                    </div>
                  </div>
                ))}
              </div>
              <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 14, padding: '0 4px'}}>
                <div style={{fontSize: 10, color: 'oklch(0.86 0.06 60)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700}}>BET LINE · 5</div>
                <div className="mono tnum" style={{fontSize: 14, color: 'var(--gold)', fontWeight: 700}}>WIN ${spin>0?'1,500':'0'}.00</div>
              </div>
            </div>

            {/* Bet controls */}
            <div style={{display: 'flex', alignItems: 'center', gap: 10, padding: 14, background: 'oklch(0 0 0 / 0.4)', borderRadius: 12, border: '1px solid oklch(0.84 0.16 86 / 0.2)'}}>
              <div style={{textAlign: 'center'}}>
                <div style={{fontSize: 9, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.1em'}}>Bet</div>
                <div style={{display: 'flex', alignItems: 'center', gap: 6, marginTop: 4}}>
                  <button className="chip" style={{cursor: 'pointer', width: 28, height: 28, justifyContent: 'center'}}>−</button>
                  <span className="mono tnum" style={{fontSize: 18, color: 'var(--gold)', fontWeight: 700, minWidth: 60, textAlign: 'center'}}>$50</span>
                  <button className="chip" style={{cursor: 'pointer', width: 28, height: 28, justifyContent: 'center'}}>+</button>
                </div>
              </div>
              <div style={{width: 1, height: 36, background: 'oklch(0.84 0.16 86 / 0.2)'}}></div>
              <button className="btn-gold" style={{height: 56, fontSize: 18, padding: '0 36px'}}>SPIN</button>
              <div style={{width: 1, height: 36, background: 'oklch(0.84 0.16 86 / 0.2)'}}></div>
              <button className="chip" style={{cursor: 'pointer', height: 30}}>AUTO ×10</button>
              <button className="chip" style={{cursor: 'pointer', height: 30}}>MAX</button>
            </div>
          </div>

          {/* Right pane: feed + paytable */}
          <div style={{display: 'flex', flexDirection: 'column', gap: 12}}>
            <div className="casino-card" style={{padding: 14}}>
              <div style={{fontSize: 11, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700}}>Wager progress</div>
              <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginTop: 6}}>
                <span className="mono tnum" style={{fontSize: 18, color: 'var(--fg-0)', fontWeight: 700}}>$150 / $3,730</span>
                <span className="mono tnum" style={{fontSize: 11, color: 'var(--fg-3)'}}>4%</span>
              </div>
              <div style={{height: 4, background: 'oklch(0 0 0 / 0.5)', borderRadius: 2, marginTop: 6, overflow: 'hidden'}}>
                <div style={{width: '4%', height: '100%', background: 'linear-gradient(90deg, var(--gold), var(--neon))'}}></div>
              </div>
              <div style={{fontSize: 10, color: 'var(--fg-3)', marginTop: 6}}>
                Reach $3,730 wagered to convert credits to withdrawable $PMC.
              </div>
            </div>

            <div className="casino-card" style={{padding: 14, flex: 1, overflow: 'hidden'}}>
              <div style={{fontSize: 11, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700, marginBottom: 8}}>Live wins</div>
              {[
                { u: 'whale.eth', g: 'Crash', amt: '+12,400' },
                { u: '0x88e…092', g: 'Slots', amt: '+1,500', you: true },
                { u: 'sat0shi', g: 'Plinko', amt: '+820' },
                { u: 'lucky_ann', g: 'Roulette', amt: '+5,200' },
                { u: 'gpt6_bull', g: 'Slots', amt: '+340' },
                { u: 'degen42', g: 'Mines', amt: '+9,200' },
              ].map((w, i) => (
                <div key={i} style={{display: 'flex', alignItems: 'center', gap: 8, fontSize: 11, padding: '5px 0', borderBottom: i<5 ? '1px solid oklch(0.84 0.16 86 / 0.1)' : 0}}>
                  <div style={{width: 16, height: 16, borderRadius: 8, background: w.you ? 'var(--gold)' : 'var(--bg-3)'}}></div>
                  <span className="mono" style={{color: w.you ? 'var(--gold)' : 'var(--fg-2)'}}>{w.u}</span>
                  <span style={{color: 'var(--fg-3)'}}>·</span>
                  <span style={{color: 'var(--fg-3)'}}>{w.g}</span>
                  <span style={{flex: 1}}></span>
                  <span className="mono tnum" style={{color: 'var(--gold)', fontWeight: 700}}>{w.amt}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

// ─── Loss / re-bet screen ────────────────────────────────────────────
function LossRelance() {
  return (
    <WindowChrome casino>
      <div className="casino-shell">
        <div className="topbar" style={{background: 'oklch(0.13 0.04 310)', borderBottomColor: 'oklch(0.84 0.16 86 / 0.2)', position: 'relative', zIndex: 2}}>
          <div style={{display: 'flex', alignItems: 'center', gap: 10}}>
            <div style={{width: 22, height: 22, borderRadius: 5, background: 'linear-gradient(135deg, var(--gold), var(--neon))'}}></div>
            <span style={{fontFamily: 'var(--font-display)', fontSize: 18, color: 'var(--gold)'}}>Diamond Slots</span>
          </div>
          <div style={{flex: 1}}></div>
          <div className="balance-pill" style={{background: 'oklch(0 0 0 / 0.4)', borderColor: 'oklch(0.66 0.21 25 / 0.4)'}}>
            <div className="coin">P</div>
            <span className="mono tnum" style={{color: 'var(--no)'}}>180.00</span>
          </div>
        </div>

        <div style={{flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 40, position: 'relative', zIndex: 2}}>
          <div style={{
            width: 540, padding: 32,
            background: 'linear-gradient(180deg, oklch(0.20 0.06 320), oklch(0.13 0.04 310))',
            border: '1px solid oklch(0.66 0.21 25 / 0.4)',
            borderRadius: 16,
            textAlign: 'center',
            position: 'relative', overflow: 'hidden',
          }}>
            <div style={{position: 'absolute', inset: 0, background: 'radial-gradient(circle at top, oklch(0.66 0.21 25 / 0.15), transparent 60%)', pointerEvents: 'none'}}></div>

            <div style={{fontSize: 11, color: 'var(--no)', textTransform: 'uppercase', letterSpacing: '0.14em', fontWeight: 700, position: 'relative'}}>
              Tough break
            </div>
            <h2 style={{fontFamily: 'var(--font-display)', fontSize: 38, color: 'var(--fg-0)', margin: '10px 0 6px', position: 'relative'}}>
              You're <span style={{color: 'var(--no)'}}>$3,550</span> down<br/>from your peak.
            </h2>
            <p style={{fontSize: 13, color: 'var(--fg-2)', position: 'relative'}}>
              But the next spin is independent. One hit on <span className="mono" style={{color: 'var(--gold)'}}>💎💎💎</span> and you're back in the green.
            </p>

            <div style={{
              display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8,
              margin: '18px 0', padding: '14px 0',
              borderTop: '1px solid oklch(1 0 0 / 0.06)',
              borderBottom: '1px solid oklch(1 0 0 / 0.06)',
              position: 'relative',
            }}>
              <div>
                <div style={{fontSize: 10, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Started with</div>
                <div className="mono tnum" style={{fontSize: 18, color: 'var(--fg-1)'}}>$3,730</div>
              </div>
              <div>
                <div style={{fontSize: 10, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Current</div>
                <div className="mono tnum" style={{fontSize: 18, color: 'var(--no)'}}>$180</div>
              </div>
              <div>
                <div style={{fontSize: 10, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em'}}>Spins left to wager</div>
                <div className="mono tnum" style={{fontSize: 18, color: 'var(--gold)'}}>72</div>
              </div>
            </div>

            <div style={{
              padding: 14, borderRadius: 10, position: 'relative',
              background: 'linear-gradient(135deg, oklch(0.84 0.16 86 / 0.12), oklch(0.68 0.28 340 / 0.12))',
              border: '1px dashed oklch(0.84 0.16 86 / 0.5)',
            }}>
              <div style={{fontSize: 11, color: 'var(--gold)', textTransform: 'uppercase', letterSpacing: '0.12em', fontWeight: 700}}>★ Comeback boost</div>
              <div style={{fontSize: 16, color: 'var(--fg-0)', marginTop: 4}}>
                Re-deposit <span className="mono tnum" style={{color: 'var(--gold)', fontWeight: 700}}>$200+</span> and we'll match <span style={{color: 'var(--gold)', fontWeight: 700}}>+50%</span>
              </div>
              <div style={{fontSize: 11, color: 'var(--fg-2)', marginTop: 2}}>Plus 20 free spins on Diamond Slots.</div>
            </div>

            <div style={{display: 'flex', gap: 10, marginTop: 18, position: 'relative'}}>
              <button className="btn" style={{flex: 1, background: 'oklch(0 0 0 / 0.3)'}}>Cash out $180</button>
              <button className="btn-gold pulse" style={{flex: 1.4}}>Re-deposit & match +50% →</button>
            </div>
            <div style={{fontSize: 10, color: 'var(--fg-3)', marginTop: 12, position: 'relative'}}>
              Or back to <span style={{textDecoration: 'underline', cursor: 'pointer'}}>Markets</span> · <span style={{textDecoration: 'underline', cursor: 'pointer'}}>Take a break</span>
            </div>
          </div>
        </div>
      </div>
    </WindowChrome>
  );
}

Object.assign(window, { OTOSoft, OTOMedium, OTOHard, WheelScreen, CasinoLobby, SlotMachine, LossRelance });
