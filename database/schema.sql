create table if not exists public.profiles (id uuid primary key references auth.users(id) on delete cascade, full_name text not null, google_id text, created_at timestamptz not null default now());
create table if not exists public.rooms (number text primary key, capacity integer not null check (capacity > 0));
create table if not exists public.students (id text primary key, name text not null, course text not null, created_at timestamptz not null default now());
create table if not exists public.room_allocations (id bigint generated always as identity primary key, room_number text not null references public.rooms(number) on delete cascade, student_id text not null references public.students(id) on delete cascade, allocated_at timestamptz not null default now(), unique (room_number, student_id));
create table if not exists public.complaints (id text primary key, student_id text not null references public.students(id) on delete cascade, category text not null, description text not null, status text not null default 'OPEN' check (status in ('OPEN', 'IN_PROGRESS', 'RESOLVED')), date_raised date not null default current_date);
create table if not exists public.fee_records (id text primary key, student_id text not null references public.students(id) on delete cascade, amount numeric(10,2) not null check (amount > 0), month text not null, paid boolean not null default false, recorded_on date not null default current_date);

alter table public.profiles enable row level security;
alter table public.rooms enable row level security;
alter table public.students enable row level security;
alter table public.room_allocations enable row level security;
alter table public.complaints enable row level security;
alter table public.fee_records enable row level security;

create policy "authenticated users can manage hostel data" on public.rooms for all to authenticated using (true) with check (true);
create policy "authenticated users can manage students" on public.students for all to authenticated using (true) with check (true);
create policy "authenticated users can manage allocations" on public.room_allocations for all to authenticated using (true) with check (true);
create policy "authenticated users can manage complaints" on public.complaints for all to authenticated using (true) with check (true);
create policy "authenticated users can manage fees" on public.fee_records for all to authenticated using (true) with check (true);
create policy "users can manage own profile" on public.profiles for all to authenticated using (id = auth.uid()) with check (id = auth.uid());

insert into public.rooms (number, capacity) values ('A-101', 2), ('A-102', 2), ('B-201', 3) on conflict (number) do nothing;
insert into public.students (id, name, course) values ('STU-101', 'Aarav Sharma', 'Computer Science'), ('STU-102', 'Maya Patel', 'Information Systems'), ('STU-103', 'Rohan Mehta', 'Business Administration') on conflict (id) do nothing;